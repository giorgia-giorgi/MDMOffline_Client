package com.dbg.mdm_offline_client

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.dbg.mdm_offline_client.domain.background.BackgroundRuntime
import com.dbg.mdm_offline_client.domain.background.ConnectionStore
import com.dbg.mdm_offline_client.domain.settings.AppSettings
import com.dbg.mdm_offline_client.domain.settings.agentEnabled
import com.dbg.mdm_offline_client.presentation.App

fun main() {
    val settings = AppSettings()
    ConnectionStore.restoreFrom(settings)
    if (settings.agentEnabled) {
        BackgroundRuntime.start()
    }
    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "MDM Offline",
        ) {
            App()
        }
    }
}
