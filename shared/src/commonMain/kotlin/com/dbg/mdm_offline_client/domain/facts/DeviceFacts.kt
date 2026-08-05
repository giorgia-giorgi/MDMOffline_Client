package com.dbg.mdm_offline_client.domain.facts

import com.dbg.mdm_offline_client.domain.facts.BatteryFacts
import com.dbg.mdm_offline_client.domain.facts.HardwareFacts
import com.dbg.mdm_offline_client.domain.facts.OsFacts
import com.dbg.mdm_offline_client.domain.facts.RuntimeFacts

/**
 * Platform-specific key/value facts sent in `POST /update_info`.
 * Keys are free-form; the server merges sparsely (null value deletes a key).
 */
fun collectDeviceFacts(): Map<String, String?> = buildMap {
    putAll(OsFacts.toMap())
    putAll(HardwareFacts.toMap())
    putAll(RuntimeFacts.toMap())
    putAll(BatteryFacts.toMap())
}
