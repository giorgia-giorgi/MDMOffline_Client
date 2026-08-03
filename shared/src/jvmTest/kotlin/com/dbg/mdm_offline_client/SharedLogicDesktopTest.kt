package com.dbg.mdm_offline_client

import kotlin.test.Test
import kotlin.test.assertEquals

class SharedLogicDesktopTest {

    @Test
    fun platformIsDesktop() {
        assertEquals("Desktop", platformLabel())
    }
}
