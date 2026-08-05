package com.dbg.mdm_offline_client.facts

actual object RuntimeFacts {
    fun javaVersion(): String? = System.getProperty("java.version").orNullIfBlank()

    fun userName(): String? = System.getProperty("user.name").orNullIfBlank()

    actual fun toMap(): Map<String, String?> = buildMap {
        put(DeviceFactKeys.JAVA_VERSION, javaVersion())
        put(DeviceFactKeys.USER_NAME, userName())
    }
}
