package com.dbg.mdm_offline_client.background

/**
 * Parent background runtime. Starts once from process entry
 * (`main` on JVM, `Application.onCreate` on Android) and owns all
 * long-lived child workers. Must not be started from UI / ViewModel.
 *
 * On start: waits for tutorial, then [ServerEnrollment.ensureConnected],
 * then the `/update_info` loop.
 */
expect object BackgroundRuntime {
    fun start()
}
