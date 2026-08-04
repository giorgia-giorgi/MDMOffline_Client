package com.dbg.mdm_offline_client

import android.app.Application
import com.dbg.mdm_offline_client.background.BackgroundRuntime

class MdmOfflineApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AndroidContextHolder.init(this)
        BackgroundRuntime.start()
    }
}
