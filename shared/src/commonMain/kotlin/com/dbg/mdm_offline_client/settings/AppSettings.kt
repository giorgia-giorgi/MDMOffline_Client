package com.dbg.mdm_offline_client.settings

import com.dbg.mdm_offline_client.model.AppLanguage

expect class AppSettings() {
    var tutorialCompleted: Boolean
    var deviceId: String
    var deviceName: String
    var lastServerBaseUrl: String?

    /** Always follows the device/OS locale. Unsupported languages fall back to English. */
    fun systemLanguage(): AppLanguage
}

/** Ensures a stable deviceId exists and returns it. */
fun AppSettings.ensureDeviceId(generate: () -> String): String {
    val existing = deviceId.trim()
    if (existing.isNotEmpty()) return existing
    val created = generate()
    deviceId = created
    return created
}
