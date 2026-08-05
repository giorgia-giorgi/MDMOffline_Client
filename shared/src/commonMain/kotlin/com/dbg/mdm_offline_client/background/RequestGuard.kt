package com.dbg.mdm_offline_client.background

/**
 * Runs [request] and falls back to [ServerEnrollment.ensureConnected] on failure.
 * Use for every outbound client→server request outside of enrollment itself.
 */
suspend fun withEnsureConnected(request: suspend () -> Boolean): Boolean {
    val ok = request()
    if (!ok) {
        ServerEnrollment.ensureConnected()
    }
    return ok
}
