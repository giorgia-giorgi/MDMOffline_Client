package com.dbg.mdm_offline_client.domain.update

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.PowerManager
import android.provider.Settings
import com.dbg.mdm_offline_client.AndroidContextHolder
import java.util.concurrent.atomic.AtomicBoolean

/** True when the OS will not throttle this app under Doze / App Standby. */
fun isIgnoringBatteryOptimizations(context: Context = AndroidContextHolder.requireContext()): Boolean {
    val pm = context.getSystemService(PowerManager::class.java) ?: return false
    return pm.isIgnoringBatteryOptimizations(context.packageName)
}

private val batteryOptPromptedThisProcess = AtomicBoolean(false)

/**
 * Opens the system sheet to exempt this app from battery optimizations.
 * Asks at most once per process; no-op if already exempt.
 */
fun requestIgnoreBatteryOptimizations(context: Context = AndroidContextHolder.requireContext()) {
    if (isIgnoringBatteryOptimizations(context)) return
    if (!batteryOptPromptedThisProcess.compareAndSet(false, true)) return
    val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
        data = Uri.parse("package:${context.packageName}")
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    runCatching { context.startActivity(intent) }
}
