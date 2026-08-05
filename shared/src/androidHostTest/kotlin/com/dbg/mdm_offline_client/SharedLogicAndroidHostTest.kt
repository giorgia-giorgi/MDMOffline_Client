package com.dbg.mdm_offline_client

import com.dbg.mdm_offline_client.domain.platformLabel
import kotlin.test.Test
import kotlin.test.assertEquals

class SharedLogicAndroidHostTest {

    @Test
    fun platformIsAndroid() {
        assertEquals("Android", platformLabel())
    }
}
