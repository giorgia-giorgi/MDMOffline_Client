package com.dbg.mdm_offline_client.domain

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform

/** Desktop Compose target (Windows/macOS/Linux). Android stays false. */
expect fun isJvmPlatform(): Boolean