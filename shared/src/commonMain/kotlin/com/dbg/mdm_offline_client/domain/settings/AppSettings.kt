package com.dbg.mdm_offline_client.domain.settings

import com.dbg.mdm_offline_client.domain.model.AppLanguage
import com.dbg.mdm_offline_client.domain.model.ConnectionPhase
import com.dbg.mdm_offline_client.domain.newDeviceId

expect class AppSettings() {
    var tutorialCompleted: Boolean
    var deviceId: String
    var deviceName: String
    var lastServerBaseUrl: String?

    /** Persisted agent status: [ConnectionPhase.Idle], [ConnectionPhase.Discovering], or [ConnectionPhase.Connected]. */
    var connectionPhase: ConnectionPhase

    /** Always follows the device/OS locale. Unsupported languages fall back to English. */
    fun systemLanguage(): AppLanguage
}

/** Ensures a stable deviceId exists and returns it. */
fun AppSettings.ensureDeviceId(): String {
    val existing = deviceId.trim()
    if (existing.isNotEmpty()) return existing
    val created = newDeviceId()
    deviceId = created
    return created
}

val AppSettings.agentEnabled: Boolean
    get() = connectionPhase != ConnectionPhase.Idle
