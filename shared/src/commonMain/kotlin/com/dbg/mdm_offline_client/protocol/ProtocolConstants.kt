package com.dbg.mdm_offline_client.protocol

object ProtocolConstants {
    const val HTTP_PORT = 9876
    const val UDP_PORT = 9877

    /** How often the client POSTs `/update_info` while connected. */
    const val UPDATE_INFO_INTERVAL_MS = 10 * 60 * 1000L

    /** Payload clients broadcast to ask for the server address. */
    const val DISCOVER_REQUEST = "MDM_DISCOVER"

    /** Reply prefix: `MDM_SERVER|<localIp>|<httpPort>` */
    const val DISCOVER_RESPONSE_PREFIX = "MDM_SERVER"
}

data class DiscoverReply(
    val localIp: String,
    val httpPort: Int,
) {
    val baseUrl: String get() = "http://$localIp:$httpPort"
}

fun parseDiscoverReply(raw: String): DiscoverReply? {
    val parts = raw.trim().split('|')
    if (parts.size < 3) return null
    if (parts[0] != ProtocolConstants.DISCOVER_RESPONSE_PREFIX) return null
    val port = parts[2].toIntOrNull() ?: return null
    if (parts[1].isBlank()) return null
    return DiscoverReply(localIp = parts[1].trim(), httpPort = port)
}

/**
 * Normalizes user input to an HTTP base URL.
 * Accepts `192.168.1.10`, `192.168.1.10:9876`, or `http://192.168.1.10:9876`.
 */
fun normalizeServerBaseUrl(input: String): String? {
    val trimmed = input.trim()
    if (trimmed.isEmpty()) return null

    var value = trimmed
    if (!value.startsWith("http://", ignoreCase = true) &&
        !value.startsWith("https://", ignoreCase = true)
    ) {
        value = "http://$value"
    }

    val withoutScheme = value.substringAfter("://")
    if (withoutScheme.isBlank() || withoutScheme.contains(' ')) return null

    val hostPort = withoutScheme.trimEnd('/')
    val host = hostPort.substringBefore(':')
    if (host.isBlank()) return null

    val hasExplicitPort = hostPort.contains(':') &&
        hostPort.substringAfterLast(':').toIntOrNull() != null
    return if (hasExplicitPort) {
        "http://$hostPort"
    } else {
        "http://$host:${ProtocolConstants.HTTP_PORT}"
    }
}
