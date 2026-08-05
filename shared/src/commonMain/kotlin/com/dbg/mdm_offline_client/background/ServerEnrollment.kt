package com.dbg.mdm_offline_client.background

import com.dbg.mdm_offline_client.api.MdmApi
import com.dbg.mdm_offline_client.api.MdmRegisterRejectedException
import com.dbg.mdm_offline_client.api.RegisterRequest
import com.dbg.mdm_offline_client.api.discoverServerBaseUrl
import com.dbg.mdm_offline_client.api.isStatusReachable
import com.dbg.mdm_offline_client.appVersionName
import com.dbg.mdm_offline_client.defaultDeviceName
import com.dbg.mdm_offline_client.i18n.stringsFor
import com.dbg.mdm_offline_client.model.ConnectionPhase
import com.dbg.mdm_offline_client.newDeviceId
import com.dbg.mdm_offline_client.platformLabel
import com.dbg.mdm_offline_client.settings.AppSettings
import com.dbg.mdm_offline_client.settings.ensureDeviceId
import com.dbg.mdm_offline_client.update.UpdateInfoReporter
import kotlinx.coroutines.sync.Mutex

/**
 * Ensures the device is enrolled with a reachable console.
 * Prefer a live cached `/status`; only UDP-discover + register when needed.
 */
object ServerEnrollment {

    private val ensureMutex = Mutex()

    /**
     * 1. If cached server answers `GET /status` → mark connected, done.
     * 2. Otherwise UDP discover + register.
     *
     * Skips if another [ensureConnected] is already in progress.
     */
    suspend fun ensureConnected(
        api: MdmApi = MdmApi(),
        settings: AppSettings = AppSettings(),
    ): Boolean {
        if (!ensureMutex.tryLock()) return false
        return try {
            val cached = settings.lastServerBaseUrl?.takeIf { it.isNotBlank() }
            if (cached != null) {
                ConnectionStore.update {
                    it.copy(serverBaseUrl = cached, busy = true, errorMessage = null)
                }
                if (isStatusReachable(cached)) {
                    ConnectionStore.markReachable(cached)
                    return true
                }
            }

            discoverAndRegister(api, settings)
        } finally {
            ensureMutex.unlock()
        }
    }

    suspend fun discoverAndRegister(
        api: MdmApi = MdmApi(),
        settings: AppSettings = AppSettings(),
    ): Boolean {
        val strings = stringsFor(settings.systemLanguage())
        val cached = settings.lastServerBaseUrl?.takeIf { it.isNotBlank() }

        ConnectionStore.update {
            it.copy(
                phase = ConnectionPhase.Discovering,
                busy = true,
                errorMessage = null,
                lastMessage = null,
                listedOnServer = null,
                serverBaseUrl = cached ?: it.serverBaseUrl,
            )
        }

        val discovered = runCatching { discoverServerBaseUrl() }.getOrNull()
        if (discovered.isNullOrBlank()) {
            ConnectionStore.update {
                it.copy(
                    phase = ConnectionPhase.Error,
                    busy = false,
                    errorMessage = strings.errorNoServer,
                    serverBaseUrl = cached,
                    serverReachable = false,
                    listedOnServer = false,
                )
            }
            return false
        }

        return registerAt(discovered, api, settings)
    }

    private suspend fun registerAt(
        baseUrl: String,
        api: MdmApi,
        settings: AppSettings,
    ): Boolean {
        val strings = stringsFor(settings.systemLanguage())
        val deviceId = settings.ensureDeviceId(::newDeviceId)
        var deviceName = settings.deviceName.trim()
        if (deviceName.isEmpty()) {
            deviceName = defaultDeviceName()
            settings.deviceName = deviceName
        }

        ConnectionStore.update {
            it.copy(
                phase = ConnectionPhase.Registering,
                busy = true,
                errorMessage = null,
                serverBaseUrl = baseUrl,
            )
        }

        return try {
            val response = api.register(
                baseUrl = baseUrl,
                request = RegisterRequest(
                    deviceId = deviceId,
                    deviceName = deviceName,
                    platform = platformLabel(),
                    appVersion = appVersionName(),
                ),
            )
            settings.lastServerBaseUrl = baseUrl
            ConnectionStore.markReachable(baseUrl, lastMessage = response.message)
            UpdateInfoReporter.sendOnce(api = api, settings = settings, reconnectOnFailure = false)
            true
        } catch (e: MdmRegisterRejectedException) {
            ConnectionStore.update {
                it.copy(
                    phase = ConnectionPhase.Error,
                    busy = false,
                    lastMessage = e.message,
                    errorMessage = strings.errorRegisterRejected,
                    listedOnServer = false,
                    serverReachable = false,
                )
            }
            false
        } catch (_: Exception) {
            ConnectionStore.update {
                it.copy(
                    phase = ConnectionPhase.Error,
                    busy = false,
                    errorMessage = strings.errorNetwork,
                    listedOnServer = false,
                    serverReachable = false,
                )
            }
            false
        }
    }
}
