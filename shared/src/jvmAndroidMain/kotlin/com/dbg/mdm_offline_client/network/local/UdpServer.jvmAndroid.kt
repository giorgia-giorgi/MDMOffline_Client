package com.dbg.mdm_offline_client.network.local

import com.dbg.mdm_offline_client.network.api.withUdpDiscoveryEnvironment
import com.dbg.mdm_offline_client.network.protocol.DiscoverReply
import com.dbg.mdm_offline_client.network.protocol.ProtocolConstants
import com.dbg.mdm_offline_client.network.protocol.parseDiscoverReply
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.NetworkInterface
import java.nio.charset.StandardCharsets
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference
import kotlin.concurrent.thread
import kotlin.time.Duration.Companion.milliseconds

actual object UdpServer {
    private val started = AtomicBoolean(false)
    private val discoverMutex = Mutex()
    private val pendingDiscover = AtomicReference<CompletableDeferred<DiscoverReply>?>(null)
    private val ready = CountDownLatch(1)

    @Volatile
    private var socket: DatagramSocket? = null

    actual fun start() {
        if (!started.compareAndSet(false, true)) return
        thread(name = "mdm-udp-server", isDaemon = true) {
            try {
                val sock = DatagramSocket(null).apply {
                    reuseAddress = true
                    broadcast = true
                    bind(InetSocketAddress(ProtocolConstants.CLIENT_UDP_PORT))
                }
                socket = sock
                ready.countDown()
                val buffer = ByteArray(512)
                while (!Thread.currentThread().isInterrupted && !sock.isClosed) {
                    try {
                        val packet = DatagramPacket(buffer, buffer.size)
                        sock.receive(packet)
                        val message = String(
                            packet.data,
                            packet.offset,
                            packet.length,
                            StandardCharsets.UTF_8,
                        ).trim()
                        handleInbound(message)
                    } catch (_: Exception) {
                        if (sock.isClosed) break
                    }
                }
            } catch (_: Exception) {
                ready.countDown()
            }
        }
    }

    /**
     * Broadcasts `MDM_DISCOVER` and awaits an `MDM_SERVER` reply on the always-on socket.
     */
    actual suspend fun discover(): DiscoverReply? = withContext(Dispatchers.IO) {
        ensureStarted()
        discoverMutex.withLock {
            val sock = socket ?: return@withContext null
            val deferred = CompletableDeferred<DiscoverReply>()
            pendingDiscover.set(deferred)
            try {
                withUdpDiscoveryEnvironment {
                    sendDiscoverBroadcast(sock)
                }
                withTimeoutOrNull(ProtocolConstants.DISCOVER_TIMEOUT_MS.milliseconds) {
                    deferred.await()
                }
            } finally {
                pendingDiscover.compareAndSet(deferred, null)
            }
        }
    }

    private fun ensureStarted() {
        if (!started.get()) start()
        ready.await(2, TimeUnit.SECONDS)
    }

    private fun handleInbound(message: String) {
        val reply = parseDiscoverReply(message)
        if (reply != null) {
            pendingDiscover.get()?.complete(reply)
            return
        }
    }

    private fun sendDiscoverBroadcast(sock: DatagramSocket) {
        val requestBytes = ProtocolConstants.DISCOVER_REQUEST.toByteArray(StandardCharsets.UTF_8)
        val targets = linkedSetOf(InetAddress.getByName("255.255.255.255"))
        subnetBroadcastAddresses().forEach { targets.add(it) }

        for (target in targets) {
            val packet = DatagramPacket(
                requestBytes,
                requestBytes.size,
                target,
                ProtocolConstants.UDP_PORT,
            )
            runCatching { sock.send(packet) }
        }
    }

    private fun subnetBroadcastAddresses(): List<InetAddress> {
        val result = mutableListOf<InetAddress>()
        try {
            val interfaces = NetworkInterface.getNetworkInterfaces() ?: return result
            for (iface in interfaces) {
                if (!iface.isUp || iface.isLoopback) continue
                for (address in iface.interfaceAddresses) {
                    val broadcast = address.broadcast ?: continue
                    result.add(broadcast)
                }
            }
        } catch (_: Exception) {
            // ignore
        }
        return result
    }
}
