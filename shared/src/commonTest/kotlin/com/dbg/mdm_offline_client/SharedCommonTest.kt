package com.dbg.mdm_offline_client

import com.dbg.mdm_offline_client.i18n.stringsFor
import com.dbg.mdm_offline_client.model.AppLanguage
import com.dbg.mdm_offline_client.protocol.ProtocolConstants
import com.dbg.mdm_offline_client.protocol.normalizeServerBaseUrl
import com.dbg.mdm_offline_client.protocol.parseDiscoverReply
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class SharedCommonTest {

    @Test
    fun protocolPortsMatchServer() {
        assertEquals(9876, ProtocolConstants.HTTP_PORT)
        assertEquals(9877, ProtocolConstants.UDP_PORT)
        assertEquals(9878, ProtocolConstants.CLIENT_HTTP_PORT)
        assertEquals(9879, ProtocolConstants.CLIENT_UDP_PORT)
        assertEquals(10 * 60 * 1000L, ProtocolConstants.UPDATE_INFO_INTERVAL_MS)
        assertEquals(60_000L, ProtocolConstants.STATUS_INTERVAL_MS)
        assertEquals("MDM_DISCOVER", ProtocolConstants.DISCOVER_REQUEST)
        assertEquals("MDM_SERVER", ProtocolConstants.DISCOVER_RESPONSE_PREFIX)
    }

    @Test
    fun parseDiscoverReply() {
        val parsed = parseDiscoverReply("MDM_SERVER|192.168.1.10|9876")
        assertNotNull(parsed)
        assertEquals("192.168.1.10", parsed.localIp)
        assertEquals(9876, parsed.httpPort)
        assertEquals("http://192.168.1.10:9876", parsed.baseUrl)
        assertNull(parseDiscoverReply("SERVER_ONLINE|9876"))
        assertNull(parseDiscoverReply("MDM_SERVER|only-two"))
    }

    @Test
    fun normalizeServerBaseUrl() {
        assertEquals("http://192.168.1.10:9876", normalizeServerBaseUrl("192.168.1.10"))
        assertEquals("http://192.168.1.10:9876", normalizeServerBaseUrl("http://192.168.1.10:9876"))
        assertEquals("http://10.0.0.5:9000", normalizeServerBaseUrl("10.0.0.5:9000"))
        assertNull(normalizeServerBaseUrl("   "))
    }

    @Test
    fun languageFromLocale() {
        assertEquals(AppLanguage.ITALIAN, AppLanguage.fromLocaleTag("it-IT"))
        assertEquals(AppLanguage.ENGLISH, AppLanguage.fromLocaleTag("en-US"))
        assertEquals(AppLanguage.ENGLISH, AppLanguage.fromLocaleTag("fr-FR"))
    }

    @Test
    fun privacyBadgeLocalized() {
        assertEquals("Local-only · No cloud", stringsFor(AppLanguage.ENGLISH).privacyBadge)
        assertEquals("Solo locale · Nessun cloud", stringsFor(AppLanguage.ITALIAN).privacyBadge)
        assertEquals("Start protecting", stringsFor(AppLanguage.ENGLISH).getStarted)
        assertEquals("Inizia a proteggere", stringsFor(AppLanguage.ITALIAN).getStarted)
    }
}
