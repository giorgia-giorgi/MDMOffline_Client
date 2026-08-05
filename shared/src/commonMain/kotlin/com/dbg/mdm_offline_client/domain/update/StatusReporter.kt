package com.dbg.mdm_offline_client.domain.update

import com.dbg.mdm_offline_client.network.api.isStatusReachable
import com.dbg.mdm_offline_client.domain.background.ConnectionStore
import com.dbg.mdm_offline_client.domain.background.withEnsureConnected
import com.dbg.mdm_offline_client.domain.settings.AppSettings

/**
 * Shared one-shot `GET /status` probe used by the background runtime.
 * On failure marks the connection unreachable and falls back to ensureConnected.
 */
object StatusReporter {
    /** @return true if the server answered successfully. */
    suspend fun checkOnce(
        settings: AppSettings = AppSettings(),
    ): Boolean = withEnsureConnected {
        val baseUrl = settings.lastServerBaseUrl?.takeIf { it.isNotBlank() } ?: return@withEnsureConnected false
        val ok = isStatusReachable(baseUrl)
        if (ok) {
            ConnectionStore.markReachable(baseUrl)
        } else {
            ConnectionStore.markUnreachable()
        }
        ok
    }
}
