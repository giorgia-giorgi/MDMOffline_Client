package com.dbg.mdm_offline_client.facts

expect object HardwareFacts {
    fun toMap(): Map<String, String?>
}
