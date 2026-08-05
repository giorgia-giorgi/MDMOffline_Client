package com.dbg.mdm_offline_client.domain.settings

import android.content.Context
import com.dbg.mdm_offline_client.AndroidContextHolder
import com.dbg.mdm_offline_client.domain.model.AppLanguage
import java.util.Locale

actual class AppSettings actual constructor() {
    private val prefs by lazy {
        AndroidContextHolder.requireContext()
            .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    actual var tutorialCompleted: Boolean
        get() = prefs.getBoolean(KEY_TUTORIAL, false)
        set(value) {
            prefs.edit().putBoolean(KEY_TUTORIAL, value).apply()
        }

    actual var deviceId: String
        get() = prefs.getString(KEY_DEVICE_ID, "").orEmpty()
        set(value) {
            prefs.edit().putString(KEY_DEVICE_ID, value).apply()
        }

    actual var deviceName: String
        get() = prefs.getString(KEY_DEVICE_NAME, "").orEmpty()
        set(value) {
            prefs.edit().putString(KEY_DEVICE_NAME, value).apply()
        }

    actual var lastServerBaseUrl: String?
        get() = prefs.getString(KEY_LAST_SERVER, null)?.takeIf { it.isNotBlank() }
        set(value) {
            prefs.edit().putString(KEY_LAST_SERVER, value).apply()
        }

    actual fun systemLanguage(): AppLanguage =
        AppLanguage.fromLocaleTag(Locale.getDefault().toLanguageTag())

    private companion object {
        const val PREFS_NAME = "mdm_offline_client"
        const val KEY_TUTORIAL = "tutorialCompleted"
        const val KEY_DEVICE_ID = "deviceId"
        const val KEY_DEVICE_NAME = "deviceName"
        const val KEY_LAST_SERVER = "lastServerBaseUrl"
    }
}
