package com.dbg.mdm_offline_client.domain.background

import com.dbg.mdm_offline_client.domain.model.ConnectionPhase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/** Shared connection state between background workers and the UI. */
data class ConnectionSnapshot(
    val phase: ConnectionPhase = ConnectionPhase.Idle,
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

    fun markReachable(baseUrl: String, lastMessage: String? = null) {
        _snapshot.update {
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

    fun markUnreachable(errorMessage: String? = null) {
        _snapshot.update {
            it.copy(
                phase = if (it.phase == ConnectionPhase.Connected) {
                    ConnectionPhase.Error
                } else {
                    it.phase
                },
                errorMessage = errorMessage ?: it.errorMessage,
                listedOnServer = false,
                busy = false,
                serverReachable = false,
            )
        }
    }
}
