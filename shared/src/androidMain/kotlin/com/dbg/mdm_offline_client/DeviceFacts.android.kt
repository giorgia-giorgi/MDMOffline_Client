package com.dbg.mdm_offline_client

import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build

actual fun collectDeviceFacts(): Map<String, String?> {
    val context = AndroidContextHolder.requireContext()
    val batteryManager = context.getSystemService(BatteryManager::class.java)
    val level = batteryManager?.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)

    val batteryStatus = runCatching {
        context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
    }.getOrNull()
    val status = batteryStatus?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
    val charging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
        status == BatteryManager.BATTERY_STATUS_FULL

    return buildMap {
        put("battery_level", level?.takeIf { it in 0..100 }?.toString())
        put("charging", charging.toString())
        put("manufacturer", Build.MANUFACTURER.orEmpty().ifBlank { null })
        put("model", Build.MODEL.orEmpty().ifBlank { null })
        put("os_version", Build.VERSION.RELEASE.orEmpty().ifBlank { null })
        put("os_sdk", Build.VERSION.SDK_INT.toString())
        put("os_build", Build.DISPLAY.orEmpty().ifBlank { null })
    }
}
