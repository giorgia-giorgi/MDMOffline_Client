package com.dbg.mdm_offline_client.domain.background

/**
 * Parent background runtime for the MDM agent.
 * Start when leaving [com.dbg.mdm_offline_client.domain.model.ConnectionPhase.Idle];
 * stop when returning to Idle.
 *
 * On start: brings up [com.dbg.mdm_offline_client.network.local.TcpServer] and
 * [com.dbg.mdm_offline_client.network.local.UdpServer], then [ServerEnrollment.ensureConnected],
 * then the `/status` and `/update_info` child workers.
 */
expect object BackgroundRuntime {
    fun start()
    fun stop()
}
