package com.dbg.mdm_offline_client.domain.background

import com.dbg.mdm_offline_client.domain.model.ConnectionPhase
import com.dbg.mdm_offline_client.domain.settings.AppSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/** Shared connection state between background workers and the UI. */
data class ConnectionSnapshot(
    val phase: ConnectionPhase = ConnectionPhase.Discovering,
    val serverBaseUrl: String? = null,
    val lastMessage: String? = null,
    val errorMessage: String? = null,
    val listedOnServer: Boolean? = null,
    val busy: Boolean = false,
    val serverReachable: Boolean = false,
)

object ConnectionStore {
    private val _snapshot = MutableStateFlow(ConnectionSnapshot())
    val snapshot: StateFlow<ConnectionSnapshot> = _snapshot.asStateFlow()

    fun update(transform: (ConnectionSnapshot) -> ConnectionSnapshot) {
        _snapshot.update(transform)
    }

    /** Hydrate in-memory state from persisted settings (call once at process start). */
    fun restoreFrom(settings: AppSettings) {
        _snapshot.value = ConnectionSnapshot(
            phase = settings.connectionPhase,
            serverBaseUrl = settings.lastServerBaseUrl,
        )
    }

    fun markReachable(
        baseUrl: String,
        lastMessage: String? = null,
        settings: AppSettings = AppSettings(),
    ) {
        if (settings.connectionPhase == ConnectionPhase.Idle) return
        settings.connectionPhase = ConnectionPhase.Connected
        _snapshot.update {
            if (it.phase == ConnectionPhase.Idle) return@update it
            it.copy(
                phase = ConnectionPhase.Connected,
                serverBaseUrl = baseUrl,
                lastMessage = lastMessage ?: it.lastMessage,
                errorMessage = null,
                listedOnServer = true,
                busy = false,
                serverReachable = true,
            )
        }
    }

    fun markUnreachable(
        errorMessage: String? = null,
        settings: AppSettings = AppSettings(),
    ) {
        _snapshot.update {
            if (it.phase == ConnectionPhase.Idle) return@update it
            val nextPhase = ConnectionPhase.Discovering
            settings.connectionPhase = nextPhase
            it.copy(
                phase = nextPhase,
                errorMessage = errorMessage ?: it.errorMessage,
                listedOnServer = false,
                busy = false,
                serverReachable = false,
            )
        }
    }

    fun markDiscovering(
        settings: AppSettings = AppSettings(),
        errorMessage: String? = null,
        busy: Boolean = true,
    ) {
        settings.connectionPhase = ConnectionPhase.Discovering
        _snapshot.update {
            it.copy(
                phase = ConnectionPhase.Discovering,
                errorMessage = errorMessage,
                busy = busy,
                serverReachable = false,
            )
        }
    }

    fun markIdle(settings: AppSettings = AppSettings()) {
        settings.connectionPhase = ConnectionPhase.Idle
        settings.lastServerBaseUrl = null
        _snapshot.update {
            ConnectionSnapshot(phase = ConnectionPhase.Idle)
        }
    }
}
