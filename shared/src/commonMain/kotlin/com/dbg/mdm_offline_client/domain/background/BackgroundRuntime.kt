package com.dbg.mdm_offline_client.domain.background

/**
 * Parent background runtime. Starts once from process entry
 * (`main` on JVM, `Application.onCreate` on Android) and owns all
 * long-lived child workers. Must not be started from UI / ViewModel.
 *
 * On start: brings up always-on [com.dbg.mdm_offline_client.network.local.TcpServer] and
 * [com.dbg.mdm_offline_client.network.local.UdpServer], then [ServerEnrollment.ensureConnected],
 * then the `/status` and `/update_info` child workers.
 */
expect object BackgroundRuntime {
    fun start()
}
