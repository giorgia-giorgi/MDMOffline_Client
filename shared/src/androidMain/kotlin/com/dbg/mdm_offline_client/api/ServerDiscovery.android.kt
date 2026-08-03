package com.dbg.mdm_offline_client.api

import android.content.Context
import android.net.wifi.WifiManager
import com.dbg.mdm_offline_client.AndroidContextHolder

/**
 * Some Android Wi‑Fi stacks filter broadcast/multicast; hold a multicast lock
 * for the duration of UDP discovery.
 */
internal actual fun <T> withUdpDiscoveryEnvironment(block: () -> T): T {
    val context = AndroidContextHolder.requireContext()
    val wifi = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as? WifiManager
    @Suppress("DEPRECATION")
    val multicastLock = wifi?.createMulticastLock("mdm-offline-discover")?.apply {
        setReferenceCounted(false)
        acquire()
    }
    return try {
        block()
    } finally {
        runCatching { multicastLock?.release() }
    }
}
