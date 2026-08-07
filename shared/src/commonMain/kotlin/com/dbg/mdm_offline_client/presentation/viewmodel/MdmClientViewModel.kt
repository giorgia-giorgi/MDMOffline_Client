package com.dbg.mdm_offline_client.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dbg.mdm_offline_client.domain.background.BackgroundRuntime
import com.dbg.mdm_offline_client.domain.background.ConnectionStore
import com.dbg.mdm_offline_client.domain.background.ServerEnrollment
import com.dbg.mdm_offline_client.domain.defaultDeviceName
import com.dbg.mdm_offline_client.presentation.i18n.Strings
import com.dbg.mdm_offline_client.presentation.i18n.stringsFor
import com.dbg.mdm_offline_client.domain.model.ConnectionPhase
import com.dbg.mdm_offline_client.domain.platformLabel
import com.dbg.mdm_offline_client.domain.settings.AppSettings
import com.dbg.mdm_offline_client.domain.settings.agentEnabled
import com.dbg.mdm_offline_client.domain.settings.ensureDeviceId
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ClientUiState(
    val strings: Strings = stringsFor(com.dbg.mdm_offline_client.domain.model.AppLanguage.ENGLISH),
    val phase: ConnectionPhase = ConnectionPhase.Discovering,
    val serverBaseUrl: String? = null,
    val deviceId: String = "",
    val deviceName: String = "",
    val platform: String = "",
    val lastMessage: String? = null,
    val errorMessage: String? = null,
    val listedOnServer: Boolean? = null,
    val busy: Boolean = false,
)

/** UI mirror of [ConnectionStore]. Toggles the MDM agent between Idle and Discovering. */
class MdmClientViewModel(
    private val settings: AppSettings = AppSettings(),
) : ViewModel() {

    private val deviceId = settings.ensureDeviceId()
    private val deviceName = settings.deviceName.trim().ifEmpty {
        defaultDeviceName().also { settings.deviceName = it }
    }
    private val platform = platformLabel()
    private val strings = stringsFor(settings.systemLanguage())

    val state: StateFlow<ClientUiState> = ConnectionStore.snapshot
        .map { snap ->
            ClientUiState(
                strings = strings,
                phase = snap.phase,
                serverBaseUrl = snap.serverBaseUrl ?: settings.lastServerBaseUrl,
                deviceId = deviceId,
                deviceName = deviceName,
                platform = platform,
                lastMessage = snap.lastMessage,
                errorMessage = snap.errorMessage,
                listedOnServer = snap.listedOnServer,
                busy = snap.busy,
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ClientUiState(
                strings = strings,
                phase = settings.connectionPhase,
                serverBaseUrl = settings.lastServerBaseUrl,
                deviceId = deviceId,
                deviceName = deviceName,
                platform = platform,
            ),
        )

    val tutorialCompleted: Boolean
        get() = settings.tutorialCompleted

    init {
        ConnectionStore.restoreFrom(settings)
    }

    fun completeTutorial() {
        settings.tutorialCompleted = true
    }

    fun onAppForeground() {
        if (!settings.agentEnabled) return
        viewModelScope.launch { ServerEnrollment.ensureConnected() }
    }

    /** Switches between Idle (agent off) and Discovering (agent on + background service). */
    fun toggleAgent() {
        if (settings.agentEnabled) {
            stopAgent()
        } else {
            startAgent()
        }
    }

    private fun startAgent() {
        ConnectionStore.markDiscovering(settings = settings, busy = true)
        BackgroundRuntime.start()
    }

    private fun stopAgent() {
        ConnectionStore.markIdle(settings = settings)
        BackgroundRuntime.stop()
    }
}
