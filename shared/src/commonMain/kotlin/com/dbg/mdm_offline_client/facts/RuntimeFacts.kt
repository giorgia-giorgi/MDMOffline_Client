package com.dbg.mdm_offline_client.facts

expect object RuntimeFacts {
    fun toMap(): Map<String, String?>
}
