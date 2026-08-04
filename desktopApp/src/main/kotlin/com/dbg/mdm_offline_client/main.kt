package com.dbg.mdm_offline_client

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.dbg.mdm_offline_client.background.BackgroundRuntime

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