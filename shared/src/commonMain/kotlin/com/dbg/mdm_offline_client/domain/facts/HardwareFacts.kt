package com.dbg.mdm_offline_client.domain.facts

expect object HardwareFacts {
    fun toMap(): Map<String, String?>
}
