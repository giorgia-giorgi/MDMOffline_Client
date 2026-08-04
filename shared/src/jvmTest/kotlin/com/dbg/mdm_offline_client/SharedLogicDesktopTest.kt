package com.dbg.mdm_offline_client

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
        val facts = collectDeviceFacts()
        assertTrue(facts.containsKey("os_name"))
        assertTrue(!facts["os_name"].isNullOrBlank())
    }
}
