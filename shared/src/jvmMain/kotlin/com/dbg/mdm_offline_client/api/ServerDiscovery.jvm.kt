package com.dbg.mdm_offline_client.api

internal actual fun <T> withUdpDiscoveryEnvironment(block: () -> T): T = block()
