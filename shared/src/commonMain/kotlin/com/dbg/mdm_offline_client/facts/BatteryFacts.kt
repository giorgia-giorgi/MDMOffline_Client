package com.dbg.mdm_offline_client.facts

expect object BatteryFacts {
    fun toMap(): Map<String, String?>
}
