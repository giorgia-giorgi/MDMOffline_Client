package com.dbg.mdm_offline_client.domain.background

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.dbg.mdm_offline_client.AndroidContextHolder
import com.dbg.mdm_offline_client.domain.settings.AppSettings
import com.dbg.mdm_offline_client.domain.settings.agentEnabled

/** Restarts the MDM agent after reboot when it was left enabled (not Idle). */
class MdmBackgroundBootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action != Intent.ACTION_BOOT_COMPLETED) return
        AndroidContextHolder.init(context.applicationContext)
        val settings = AppSettings()
        ConnectionStore.restoreFrom(settings)
        if (settings.agentEnabled) {
            BackgroundRuntime.start()
        }
    }
}
