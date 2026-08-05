package com.dbg.mdm_offline_client.domain.facts

expect object RuntimeFacts {
    fun toMap(): Map<String, String?>
}
