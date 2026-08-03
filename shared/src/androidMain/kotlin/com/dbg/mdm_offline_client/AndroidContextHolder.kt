package com.dbg.mdm_offline_client

import android.content.Context

/**
 * Holds the application context for SharedPreferences and device info.
 * Set once from [MainActivity] / Application before UI starts.
 */
object AndroidContextHolder {
    @Volatile
    private var appContext: Context? = null

    fun init(context: Context) {
        appContext = context.applicationContext
    }

    fun requireContext(): Context =
        appContext ?: error("AndroidContextHolder.init() must be called before use")
}
