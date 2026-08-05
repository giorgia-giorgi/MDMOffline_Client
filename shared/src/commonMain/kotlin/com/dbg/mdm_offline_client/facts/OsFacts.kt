package com.dbg.mdm_offline_client.facts

expect object OsFacts {
    fun toMap(): Map<String, String?>
}
