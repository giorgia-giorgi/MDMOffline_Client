package com.dbg.mdm_offline_client

import android.app.Application
import com.dbg.mdm_offline_client.domain.background.BackgroundRuntime
import com.dbg.mdm_offline_client.domain.background.ConnectionStore
import com.dbg.mdm_offline_client.domain.settings.AppSettings
import com.dbg.mdm_offline_client.domain.settings.agentEnabled

class MdmOfflineApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AndroidContextHolder.init(this)
        val settings = AppSettings()
        ConnectionStore.restoreFrom(settings)
        if (settings.agentEnabled) {
            BackgroundRuntime.start()
        }
    }
}
