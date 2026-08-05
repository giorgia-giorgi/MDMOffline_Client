package com.dbg.mdm_offline_client

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.dbg.mdm_offline_client.domain.background.BackgroundRuntime
import com.dbg.mdm_offline_client.presentation.App

fun main() {
    BackgroundRuntime.start()
    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "MDM Offline",
        ) {
            App()
        }
    }
}