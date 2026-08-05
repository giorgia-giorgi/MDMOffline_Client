package com.dbg.mdm_offline_client.facts

import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import com.dbg.mdm_offline_client.AndroidContextHolder

actual object BatteryFacts {
    fun batteryLevel(): String? {
        val context = AndroidContextHolder.requireContext()
        val batteryManager = context.getSystemService(BatteryManager::class.java)
        val level = batteryManager?.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
        return level?.takeIf { it in 0..100 }?.toString()
    }

    fun charging(): String? = isCharging().toString()

    actual fun toMap(): Map<String, String?> = buildMap {
        put(DeviceFactKeys.BATTERY_LEVEL, batteryLevel())
        put(DeviceFactKeys.CHARGING, charging())
    }

    private fun isCharging(): Boolean {
        val context = AndroidContextHolder.requireContext()
        val batteryStatus = runCatching {
            context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        }.getOrNull()
        val status = batteryStatus?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
        return status == BatteryManager.BATTERY_STATUS_CHARGING ||
            status == BatteryManager.BATTERY_STATUS_FULL
    }
}
