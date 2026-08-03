package com.dbg.mdm_offline_client.settings

import com.dbg.mdm_offline_client.model.AppLanguage
import platform.Foundation.NSLocale
import platform.Foundation.NSUserDefaults
import platform.Foundation.preferredLanguages

actual class AppSettings actual constructor() {
    private val defaults = NSUserDefaults.standardUserDefaults

    actual var tutorialCompleted: Boolean
        get() = defaults.boolForKey(KEY_TUTORIAL)
        set(value) {
            defaults.setBool(value, KEY_TUTORIAL)
        }

    actual var deviceId: String
        get() = defaults.stringForKey(KEY_DEVICE_ID).orEmpty()
        set(value) {
            defaults.setObject(value, KEY_DEVICE_ID)
        }

    actual var deviceName: String
        get() = defaults.stringForKey(KEY_DEVICE_NAME).orEmpty()
        set(value) {
            defaults.setObject(value, KEY_DEVICE_NAME)
        }

    actual var lastServerBaseUrl: String?
        get() = defaults.stringForKey(KEY_LAST_SERVER)?.takeIf { it.isNotBlank() }
        set(value) {
            if (value.isNullOrBlank()) {
                defaults.removeObjectForKey(KEY_LAST_SERVER)
            } else {
                defaults.setObject(value, KEY_LAST_SERVER)
            }
        }

    actual fun systemLanguage(): AppLanguage {
        val tag = (NSLocale.preferredLanguages.firstOrNull() as? String).orEmpty()
        return AppLanguage.fromLocaleTag(tag)
    }

    private companion object {
        const val KEY_TUTORIAL = "tutorialCompleted"
        const val KEY_DEVICE_ID = "deviceId"
        const val KEY_DEVICE_NAME = "deviceName"
        const val KEY_LAST_SERVER = "lastServerBaseUrl"
    }
}
