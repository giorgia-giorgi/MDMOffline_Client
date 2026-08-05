package com.dbg.mdm_offline_client.domain

import java.util.UUID

class JVMPlatform : Platform {
    override val name: String = "Java ${System.getProperty("java.version")}"
}

actual fun getPlatform(): Platform = JVMPlatform()

actual fun defaultDeviceName(): String =
    System.getProperty("user.name")?.takeIf { it.isNotBlank() }?.let { "$it PC" }
        ?: "Desktop device"

actual fun platformLabel(): String = "Desktop"

actual fun appVersionName(): String = "1.0.0"

actual fun newDeviceId(): String = UUID.randomUUID().toString()
