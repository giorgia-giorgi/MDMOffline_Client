package com.dbg.mdm_offline_client

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "MDM Offline",
    ) {
        App()
    }
}