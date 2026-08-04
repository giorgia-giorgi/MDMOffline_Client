package com.dbg.mdm_offline_client.update

import com.dbg.mdm_offline_client.api.MdmApi
import com.dbg.mdm_offline_client.api.UpdateInfoRequest
import com.dbg.mdm_offline_client.appVersionName
import com.dbg.mdm_offline_client.background.ConnectionStore
import com.dbg.mdm_offline_client.collectDeviceFacts
import com.dbg.mdm_offline_client.newDeviceId
import com.dbg.mdm_offline_client.settings.AppSettings
import com.dbg.mdm_offline_client.settings.ensureDeviceId

/**
 * Shared one-shot reporter used by the background runtime.
 * On failure marks the connection unreachable so callers can fall back to discover.
 */
object UpdateInfoReporter {
    /** @return true if the server accepted the update. */
    suspend fun sendOnce(
        api: MdmApi = MdmApi(),
        settings: AppSettings = AppSettings(),
    ): Boolean {
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
}
