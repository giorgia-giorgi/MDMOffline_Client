package com.dbg.mdm_offline_client.domain.facts

actual object OsFacts {

    actual fun toMap(): Map<String, String?> = buildMap {
        put(DeviceFactKeys.OS_VERSION, "Test")
        put(DeviceFactKeys.OS_SDK, null)
        put(DeviceFactKeys.OS_BUILD, null)
    }
}
