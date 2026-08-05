package com.dbg.mdm_offline_client.domain.facts

actual object OsFacts {
    fun osName(): String? = System.getProperty("os.name").orNullIfBlank()

    fun osVersion(): String? = System.getProperty("os.version").orNullIfBlank()

    fun osArch(): String? = System.getProperty("os.arch").orNullIfBlank()

    actual fun toMap(): Map<String, String?> = buildMap {
        put(DeviceFactKeys.OS_NAME, osName())
        put(DeviceFactKeys.OS_VERSION, osVersion())
        put(DeviceFactKeys.OS_ARCH, osArch())
    }
}
