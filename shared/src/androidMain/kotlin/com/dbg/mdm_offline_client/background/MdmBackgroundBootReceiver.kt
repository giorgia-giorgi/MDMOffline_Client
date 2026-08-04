package com.dbg.mdm_offline_client.background

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.dbg.mdm_offline_client.AndroidContextHolder

/** Restarts the parent background runtime after device reboot. */
class MdmBackgroundBootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action != Intent.ACTION_BOOT_COMPLETED) return
        AndroidContextHolder.init(context.applicationContext)
        BackgroundRuntime.start()
    }
}
