package com.dbg.mdm_offline_client

import platform.UIKit.UIDevice
import platform.Foundation.NSUUID

class IOSPlatform : Platform {
    override val name: String =
        UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
}

actual fun getPlatform(): Platform = IOSPlatform()

actual fun defaultDeviceName(): String =
    UIDevice.currentDevice.name.takeIf { it.isNotBlank() } ?: "iOS device"

actual fun platformLabel(): String = "iOS"

actual fun appVersionName(): String = "1.0.0"

actual fun newDeviceId(): String = NSUUID().UUIDString
