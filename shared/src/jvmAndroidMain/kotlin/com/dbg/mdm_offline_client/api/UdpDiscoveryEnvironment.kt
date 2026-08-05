package com.dbg.mdm_offline_client.api

/**
 * Platform-specific environment around a UDP discover attempt
 * (e.g. Android Wi‑Fi multicast lock).
 */
internal expect fun <T> withUdpDiscoveryEnvironment(block: () -> T): T
