package com.dbg.mdm_offline_client

actual fun collectDeviceFacts(): Map<String, String?> =
    buildMap {
        put("os_name", System.getProperty("os.name")?.ifBlank { null })
        put("os_version", System.getProperty("os.version")?.ifBlank { null })
        put("os_arch", System.getProperty("os.arch")?.ifBlank { null })
        put("java_version", System.getProperty("java.version")?.ifBlank { null })
        put("user_name", System.getProperty("user.name")?.ifBlank { null })
        put("cpu_cores", Runtime.getRuntime().availableProcessors().toString())
    }
