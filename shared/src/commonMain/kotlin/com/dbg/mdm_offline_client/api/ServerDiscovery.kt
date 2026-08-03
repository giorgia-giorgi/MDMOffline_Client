package com.dbg.mdm_offline_client.api

import io.ktor.client.request.get
import io.ktor.http.isSuccess

/**
 * Discovers the MDM Offline desktop console on the LAN.
 * Broadcasts UDP `MDM_DISCOVER` and parses `MDM_SERVER|ip|port`.
 *
 * Note (Android): UDP broadcast receive may require Wi‑Fi multicast lock
 * and cleartext HTTP; see AndroidManifest usesCleartextTraffic.
 */
expect suspend fun discoverServerBaseUrl(): String?

suspend fun isStatusReachable(baseUrl: String): Boolean {
    val client = createHttpClient()
    try {
        val response = client.get("$baseUrl/status")
        return response.status.isSuccess()
    } catch (_: Exception) {
        return false
    } finally {
        client.close()
    }
}
