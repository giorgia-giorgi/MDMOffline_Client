package com.dbg.mdm_offline_client.domain.update

import com.dbg.mdm_offline_client.network.api.MdmApi
import com.dbg.mdm_offline_client.network.api.UpdateInfoRequest
import com.dbg.mdm_offline_client.domain.appVersionName
import com.dbg.mdm_offline_client.domain.background.ConnectionStore
import com.dbg.mdm_offline_client.domain.background.withEnsureConnected
import com.dbg.mdm_offline_client.domain.facts.collectDeviceFacts
import com.dbg.mdm_offline_client.domain.newDeviceId
import com.dbg.mdm_offline_client.domain.settings.AppSettings
import com.dbg.mdm_offline_client.domain.settings.ensureDeviceId

/**
 * Shared one-shot reporter used by the background runtime.
 * On failure marks the connection unreachable and falls back to ensureConnected.
 */
object UpdateInfoReporter {
    /** @return true if the server accepted the update. */
    suspend fun sendOnce(
        api: MdmApi = MdmApi(),
        settings: AppSettings = AppSettings(),
        reconnectOnFailure: Boolean = true,
    ): Boolean {
        suspend fun request(): Boolean {
            val baseUrl = settings.lastServerBaseUrl?.takeIf { it.isNotBlank() } ?: return false
            val deviceId = settings.ensureDeviceId(::newDeviceId)
            if (deviceId.isBlank()) return false

            val result = runCatching {
                api.updateInfo(
                    baseUrl = baseUrl,
                    request = UpdateInfoRequest(
                        deviceId = deviceId,
                        appVersion = appVersionName(),
                        facts = collectDeviceFacts(),
                    ),
                )
            }
            return if (result.isSuccess) {
                ConnectionStore.markReachable(baseUrl)
                true
            } else {
                ConnectionStore.markUnreachable()
                false
            }
        }
        return if (reconnectOnFailure) withEnsureConnected { request() } else request()
    }
}
