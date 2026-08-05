package com.dbg.mdm_offline_client.network.api

internal actual fun <T> withUdpDiscoveryEnvironment(block: () -> T): T = block()
