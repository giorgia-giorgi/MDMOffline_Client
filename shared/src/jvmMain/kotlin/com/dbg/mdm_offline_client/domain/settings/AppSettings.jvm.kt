package com.dbg.mdm_offline_client.domain.settings

import com.dbg.mdm_offline_client.domain.model.AppLanguage
import com.dbg.mdm_offline_client.domain.model.ConnectionPhase
import java.util.Locale
import java.util.prefs.Preferences

actual class AppSettings actual constructor() {
    private val prefs: Preferences = Preferences.userRoot().node("com.dbg.mdm_offline_client")

    actual var tutorialCompleted: Boolean
        get() = prefs.getBoolean(KEY_TUTORIAL, false)
        set(value) {
            prefs.putBoolean(KEY_TUTORIAL, value)
            prefs.flush()
        }

    actual var deviceId: String
        get() = prefs.get(KEY_DEVICE_ID, "")
        set(value) {
            prefs.put(KEY_DEVICE_ID, value)
            prefs.flush()
        }

    actual var deviceName: String
        get() = prefs.get(KEY_DEVICE_NAME, "")
        set(value) {
            prefs.put(KEY_DEVICE_NAME, value)
            prefs.flush()
        }

    actual var lastServerBaseUrl: String?
        get() = prefs.get(KEY_LAST_SERVER, null)?.takeIf { it.isNotBlank() }
        set(value) {
            if (value.isNullOrBlank()) {
                prefs.remove(KEY_LAST_SERVER)
            } else {
                prefs.put(KEY_LAST_SERVER, value)
            }
            prefs.flush()
        }

    actual var connectionPhase: ConnectionPhase
        get() = prefs.get(KEY_PHASE, null)
            ?.let { raw -> ConnectionPhase.entries.find { it.name == raw } }
            ?: ConnectionPhase.Discovering
        set(value) {
            prefs.put(KEY_PHASE, value.name)
            prefs.flush()
        }

    actual fun systemLanguage(): AppLanguage =
        AppLanguage.fromLocaleTag(Locale.getDefault().toLanguageTag())

    private companion object {
        const val KEY_TUTORIAL = "tutorialCompleted"
        const val KEY_DEVICE_ID = "deviceId"
        const val KEY_DEVICE_NAME = "deviceName"
        const val KEY_LAST_SERVER = "lastServerBaseUrl"
        const val KEY_PHASE = "connectionPhase"
    }
}
