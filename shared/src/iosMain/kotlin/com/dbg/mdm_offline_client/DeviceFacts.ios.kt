package com.dbg.mdm_offline_client

import platform.UIKit.UIDevice
import platform.UIKit.UIDeviceBatteryState

actual fun collectDeviceFacts(): Map<String, String?> {
    val device = UIDevice.currentDevice
    device.batteryMonitoringEnabled = true

    val rawLevel = device.batteryLevel
    val batteryLevel = if (rawLevel < 0f) {
        null
    } else {
        ((rawLevel * 100f).toInt().coerceIn(0, 100)).toString()
    }

    val charging = when (device.batteryState) {
        UIDeviceBatteryState.UIDeviceBatteryStateCharging,
        UIDeviceBatteryState.UIDeviceBatteryStateFull,
        -> true
        else -> false
    }

    return buildMap {
        put("battery_level", batteryLevel)
        put("charging", charging.toString())
        put("model", device.model.takeIf { it.isNotBlank() })
        put("device_name", device.name.takeIf { it.isNotBlank() })
        put("os_name", device.systemName.takeIf { it.isNotBlank() })
        put("os_version", device.systemVersion.takeIf { it.isNotBlank() })
    }
}
