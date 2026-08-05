package com.dbg.mdm_offline_client.domain.facts

expect object BatteryFacts {
    fun toMap(): Map<String, String?>
}
