package com.dbg.mdm_offline_client.domain

import android.os.Build
import android.provider.Settings
import com.dbg.mdm_offline_client.AndroidContextHolder

class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
}

actual fun getPlatform(): Platform = AndroidPlatform()

actual fun isJvmPlatform(): Boolean = false

actual fun defaultDeviceName(): String {
    val manufacturer = Build.MANUFACTURER.orEmpty().trim()
    val model = Build.MODEL.orEmpty().trim()
    return when {
        model.isEmpty() -> "Android device"
        manufacturer.isEmpty() -> model
        model.startsWith(manufacturer, ignoreCase = true) -> model
        else -> "$manufacturer $model"
    }
}

actual fun platformLabel(): String = "Android"

actual fun appVersionName(): String = "1.0.0"

actual fun newDeviceId(): String {
    val context = AndroidContextHolder.requireContext()
    return Settings.Secure.getString(
        context.contentResolver,
        Settings.Secure.ANDROID_ID,
    ).orEmpty().trim()
}
