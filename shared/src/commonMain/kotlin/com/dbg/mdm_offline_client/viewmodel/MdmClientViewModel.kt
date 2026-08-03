package com.dbg.mdm_offline_client.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dbg.mdm_offline_client.api.MdmApi
import com.dbg.mdm_offline_client.api.MdmRegisterRejectedException
import com.dbg.mdm_offline_client.api.RegisterRequest
import com.dbg.mdm_offline_client.api.discoverServerBaseUrl
import com.dbg.mdm_offline_client.appVersionName
import com.dbg.mdm_offline_client.defaultDeviceName
import com.dbg.mdm_offline_client.i18n.Strings
import com.dbg.mdm_offline_client.i18n.stringsFor
import com.dbg.mdm_offline_client.model.ConnectionPhase
import com.dbg.mdm_offline_client.newDeviceId
import com.dbg.mdm_offline_client.platformLabel
import com.dbg.mdm_offline_client.settings.AppSettings
import com.dbg.mdm_offline_client.settings.ensureDeviceId
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ClientUiState(
    val strings: Strings = stringsFor(com.dbg.mdm_offline_client.model.AppLanguage.ENGLISH),
    val phase: ConnectionPhase = ConnectionPhase.Idle,
    val serverBaseUrl: String? = null,
    val deviceId: String = "",
    val deviceName: String = "",
    val platform: String = "",
    val lastMessage: String? = null,
    val errorMessage: String? = null,
    val listedOnServer: Boolean? = null,
    val busy: Boolean = false,
)

class MdmClientViewModel(
    private val settings: AppSettings = AppSettings(),
    private val api: MdmApi = MdmApi(),
) : ViewModel() {

    private val _state = MutableStateFlow(initialState())
    val state: StateFlow<ClientUiState> = _state.asStateFlow()

    val tutorialCompleted: Boolean
        get() = settings.tutorialCompleted

    private var connectJob: Job? = null

    init {
        if (settings.tutorialCompleted) {
            connect(preferCached = true)
        }
    }

    private fun initialState(): ClientUiState {
        val language = settings.systemLanguage()
        val strings = stringsFor(language)
        val deviceId = settings.ensureDeviceId(::newDeviceId)
        var deviceName = settings.deviceName.trim()
        if (deviceName.isEmpty()) {
            deviceName = defaultDeviceName()
            settings.deviceName = deviceName
        }
        return ClientUiState(
            strings = strings,
            phase = ConnectionPhase.Idle,
            serverBaseUrl = settings.lastServerBaseUrl,
            deviceId = deviceId,
            deviceName = deviceName,
            platform = platformLabel(),
        )
    }

    fun completeTutorial() {
        settings.tutorialCompleted = true
        _state.update { it.copy(errorMessage = null) }
        connect(preferCached = true)
    }

    fun onAppForeground() {
        if (!settings.tutorialCompleted) return
        connect(preferCached = true)
    }

    fun connect() = connect(preferCached = true)

    private fun connect(preferCached: Boolean) {
        connectJob?.cancel()
        connectJob = viewModelScope.launch {
            val strings = _state.value.strings
            val cached = settings.lastServerBaseUrl?.takeIf { it.isNotBlank() }

            if (preferCached && cached != null) {
                registerAt(cached)
                if (_state.value.phase == ConnectionPhase.Connected) return@launch
            }

            _state.update {
                it.copy(
                    phase = ConnectionPhase.Discovering,
                    busy = true,
                    errorMessage = null,
                    lastMessage = null,
                    listedOnServer = null,
                )
            }

            val discovered = runCatching { discoverServerBaseUrl() }.getOrNull()
            if (discovered.isNullOrBlank()) {
                _state.update {
                    it.copy(
                        phase = ConnectionPhase.Error,
                        busy = false,
                        errorMessage = strings.errorNoServer,
                        serverBaseUrl = cached,
                    )
                }
                return@launch
            }

            registerAt(discovered)
        }
    }

    private suspend fun registerAt(baseUrl: String) {
        val strings = _state.value.strings
        _state.update {
            it.copy(
                phase = ConnectionPhase.Registering,
                busy = true,
                errorMessage = null,
                serverBaseUrl = baseUrl,
            )
        }

        try {
            val status = api.fetchStatus(baseUrl)
            val request = RegisterRequest(
                deviceId = _state.value.deviceId,
                deviceName = _state.value.deviceName,
                platform = _state.value.platform,
                appVersion = appVersionName(),
            )
            val response = api.register(baseUrl, request)
            settings.lastServerBaseUrl = baseUrl

            // Refresh status after register so the PC list presence is accurate.
            val listedAfter = runCatching {
                api.fetchStatus(baseUrl).devices.any { it.id == request.deviceId }
            }.getOrElse {
                status.devices.any { it.id == request.deviceId }
            }

            _state.update {
                it.copy(
                    phase = ConnectionPhase.Connected,
                    busy = false,
                    serverBaseUrl = baseUrl,
                    lastMessage = response.message,
                    errorMessage = null,
                    listedOnServer = listedAfter,
                )
            }
        } catch (e: MdmRegisterRejectedException) {
            _state.update {
                it.copy(
                    phase = ConnectionPhase.Error,
                    busy = false,
                    lastMessage = e.message,
                    errorMessage = strings.errorRegisterRejected,
                    listedOnServer = false,
                )
            }
        } catch (_: Exception) {
            _state.update {
                it.copy(
                    phase = ConnectionPhase.Error,
                    busy = false,
                    errorMessage = strings.errorNetwork,
                    listedOnServer = false,
                )
            }
        }
    }
}
