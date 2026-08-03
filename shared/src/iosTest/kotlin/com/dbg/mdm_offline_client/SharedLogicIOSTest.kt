package com.dbg.mdm_offline_client

import kotlin.test.Test
import kotlin.test.assertEquals

class SharedLogicIOSTest {

    @Test
    fun platformIsIos() {
        assertEquals("iOS", platformLabel())
    }
}
