package com.dbg.mdm_offline_client.facts

actual object HardwareFacts {
    fun cpuCores(): String = Runtime.getRuntime().availableProcessors().toString()

    actual fun toMap(): Map<String, String?> = buildMap {
        put(DeviceFactKeys.CPU_CORES, cpuCores())
    }
}
