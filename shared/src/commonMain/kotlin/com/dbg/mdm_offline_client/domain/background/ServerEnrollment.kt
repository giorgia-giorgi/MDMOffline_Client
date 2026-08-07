package com.dbg.mdm_offline_client.domain.background

import com.dbg.mdm_offline_client.network.api.MdmApi
import com.dbg.mdm_offline_client.network.api.MdmRegisterRejectedException
import com.dbg.mdm_offline_client.network.api.RegisterRequest
import com.dbg.mdm_offline_client.network.api.discoverServerBaseUrl
import com.dbg.mdm_offline_client.network.api.isStatusReachable
import com.dbg.mdm_offline_client.domain.appVersionName
import com.dbg.mdm_offline_client.domain.defaultDeviceName
import com.dbg.mdm_offline_client.presentation.i18n.stringsFor
import com.dbg.mdm_offline_client.domain.model.ConnectionPhase
import com.dbg.mdm_offline_client.domain.platformLabel
import com.dbg.mdm_offline_client.domain.settings.AppSettings
import com.dbg.mdm_offline_client.domain.settings.agentEnabled
import com.dbg.mdm_offline_client.domain.settings.ensureDeviceId
import com.dbg.mdm_offline_client.domain.update.UpdateInfoReporter
import kotlinx.coroutines.sync.Mutex

/**
 * Ensures the device is enrolled with a reachable console.
 * Prefer a live cached `/status`; only UDP-discover + register when needed.
 * No-op while the agent is [ConnectionPhase.Idle].
 */
object ServerEnrollment {

    private val ensureMutex = Mutex()

    /**
     * 1. If cached server answers `GET /status` → mark connected, done.
     * 2. Otherwise UDP discover + register.
     *
     * Skips if another [ensureConnected] is already in progress, or agent is idle.
     */
    suspend fun ensureConnected(
        api: MdmApi = MdmApi(),
        settings: AppSettings = AppSettings(),
    ): Boolean {
        if (!settings.agentEnabled) return false
        if (!ensureMutex.tryLock()) return false
        return try {
            val deviceId = settings.ensureDeviceId()
            val cached = settings.lastServerBaseUrl?.takeIf { it.isNotBlank() }
            if (cached != null) {
                ConnectionStore.update {
                    it.copy(
                        phase = ConnectionPhase.Discovering,
                        serverBaseUrl = cached,
                        busy = true,
                        errorMessage = null,
                    )
                }
                settings.connectionPhase = ConnectionPhase.Discovering
                if (isStatusReachable(cached, deviceId)) {
                    ConnectionStore.markReachable(cached, settings = settings)
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
        if (!settings.agentEnabled) return false
        val strings = stringsFor(settings.systemLanguage())
        val cached = settings.lastServerBaseUrl?.takeIf { it.isNotBlank() }
        val deviceId = settings.ensureDeviceId()

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
        settings.connectionPhase = ConnectionPhase.Discovering

        val discovered = runCatching { discoverServerBaseUrl(deviceId) }.getOrNull()
        if (discovered.isNullOrBlank()) {
            ConnectionStore.markDiscovering(
                settings = settings,
                errorMessage = strings.errorNoServer,
                busy = false,
            )
            ConnectionStore.update {
                it.copy(
                    serverBaseUrl = cached,
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
        if (!settings.agentEnabled) return false
        val strings = stringsFor(settings.systemLanguage())
        val deviceId = settings.ensureDeviceId()
        var deviceName = settings.deviceName.trim()
        if (deviceName.isEmpty()) {
            deviceName = defaultDeviceName()
            settings.deviceName = deviceName
        }

        ConnectionStore.update {
            it.copy(
                phase = ConnectionPhase.Discovering,
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
            ConnectionStore.markReachable(baseUrl, lastMessage = response.message, settings = settings)
            UpdateInfoReporter.sendOnce(api = api, settings = settings, reconnectOnFailure = false)
            true
        } catch (e: MdmRegisterRejectedException) {
            ConnectionStore.markDiscovering(
                settings = settings,
                errorMessage = strings.errorRegisterRejected,
                busy = false,
            )
            ConnectionStore.update {
                it.copy(
                    lastMessage = e.message,
                    listedOnServer = false,
                )
            }
            false
        } catch (_: Exception) {
            ConnectionStore.markDiscovering(
                settings = settings,
                errorMessage = strings.errorNetwork,
                busy = false,
            )
            ConnectionStore.update {
                it.copy(listedOnServer = false)
            }
            false
        }
    }
}
