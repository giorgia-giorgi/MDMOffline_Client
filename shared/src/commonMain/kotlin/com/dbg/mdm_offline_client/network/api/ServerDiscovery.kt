package com.dbg.mdm_offline_client.network.api

import com.dbg.mdm_offline_client.network.local.UdpServer
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.isSuccess

/**
 * Discovers the MDM Offline desktop console on the LAN via the always-on [UdpServer]
 * (`MDM_DISCOVER` → `MDM_SERVER|ip|port`), then verifies `GET /status`.
 */
suspend fun discoverServerBaseUrl(deviceId: String, instance: UdpServer = UdpServer): String? {
    val discovered = instance.discover() ?: return null
    val baseUrl = discovered.baseUrl
    return if (isStatusReachable(baseUrl, deviceId)) baseUrl else null
}

suspend fun isStatusReachable(baseUrl: String, deviceId: String): Boolean {
    val client = createHttpClient()
    try {
        val response = client.get("$baseUrl/status") {
            parameter("deviceId", deviceId)
        }
        return response.status.isSuccess()
    } catch (_: Exception) {
        return false
    } finally {
        client.close()
    }
}
