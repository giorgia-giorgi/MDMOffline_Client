package com.dbg.mdm_offline_client.net

import com.dbg.mdm_offline_client.protocol.DiscoverReply

/**
 * Always-on client UDP socket.
 *
 * Listens on [com.dbg.mdm_offline_client.protocol.ProtocolConstants.CLIENT_UDP_PORT]
 * for inbound datagrams (discover replies today; more message types later).
 * Call [discover] from anywhere to broadcast `MDM_DISCOVER` and await a reply.
 *
 * Started once from [com.dbg.mdm_offline_client.background.BackgroundRuntime].
 */
expect object UdpServer {
    fun start()

    /**
     * Broadcasts `MDM_DISCOVER` and waits for an `MDM_SERVER|ip|port` reply.
     * Returns null on timeout / failure. Safe to call while the receive loop runs.
     */
    suspend fun discover(): DiscoverReply?
}
