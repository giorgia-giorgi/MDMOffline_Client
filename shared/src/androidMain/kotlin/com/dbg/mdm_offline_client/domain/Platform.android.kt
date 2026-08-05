package com.dbg.mdm_offline_client.domain

import android.os.Build
import java.util.UUID

class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
}

actual fun getPlatform(): Platform = AndroidPlatform()

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

actual fun newDeviceId(): String = UUID.randomUUID().toString()
