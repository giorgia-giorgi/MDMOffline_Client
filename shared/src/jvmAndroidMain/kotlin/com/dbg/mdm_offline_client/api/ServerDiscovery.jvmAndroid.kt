package com.dbg.mdm_offline_client.api

import com.dbg.mdm_offline_client.protocol.DiscoverReply
import com.dbg.mdm_offline_client.protocol.ProtocolConstants
import com.dbg.mdm_offline_client.protocol.parseDiscoverReply
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.NetworkInterface
import java.net.SocketTimeoutException
import java.nio.charset.StandardCharsets

/**
 * Platform-specific environment around a UDP discover attempt
 * (e.g. Android Wi‑Fi multicast lock).
 */
internal expect fun <T> withUdpDiscoveryEnvironment(block: () -> T): T

actual suspend fun discoverServerBaseUrl(): String? =
    withContext(Dispatchers.IO) {
        val discovered = withUdpDiscoveryEnvironment { udpDiscover() }
        if (discovered != null) {
            val baseUrl = discovered.baseUrl
            if (isStatusReachable(baseUrl)) {
                return@withContext baseUrl
            }
        }
        null
    }

internal fun udpDiscover(): DiscoverReply? {
    var socket: DatagramSocket? = null
    try {
        socket = DatagramSocket()
        socket.broadcast = true
        socket.soTimeout = 2_000

        val requestBytes = ProtocolConstants.DISCOVER_REQUEST.toByteArray(StandardCharsets.UTF_8)
        val targets = linkedSetOf(InetAddress.getByName("255.255.255.255"))
        subnetBroadcastAddresses().forEach { targets.add(it) }

        for (target in targets){
            val response = DatagramPacket(
                requestBytes,
                requestBytes.size,
                InetAddress.getByAddress(target.address),
                ProtocolConstants.UDP_PORT
            )
            socket.send(response)
        }
        val buffer = ByteArray(512)
        val request = DatagramPacket(buffer, buffer.size)
        socket.receive(request)
        val message = String(request.data, 0, request.length, StandardCharsets.UTF_8).trim()
        val parts = message.split("|")
        val address = parts[1]
        val port = parts[2].toInt()

        return DiscoverReply(address, port)
    } catch (_: Exception) {
        return null
    } finally {
        runCatching { socket?.close() }
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
