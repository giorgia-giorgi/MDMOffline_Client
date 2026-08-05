package com.dbg.mdm_offline_client.domain.facts

expect object OsFacts {
    fun toMap(): Map<String, String?>
}
