package com.dbg.mdm_offline_client

import com.dbg.mdm_offline_client.domain.facts.OsFacts
import com.dbg.mdm_offline_client.domain.facts.collectDeviceFacts
import com.dbg.mdm_offline_client.domain.platformLabel
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SharedLogicDesktopTest {

    @Test
    fun platformIsDesktop() {
        assertEquals("Desktop", platformLabel())
    }

    @Test
    fun desktopFactsIncludeOsName() {
        assertTrue(!OsFacts.osName().isNullOrBlank())
        val facts = collectDeviceFacts()
        assertTrue(facts.containsKey("os_name"))
        assertEquals(OsFacts.osName(), facts["os_name"])
    }
}
