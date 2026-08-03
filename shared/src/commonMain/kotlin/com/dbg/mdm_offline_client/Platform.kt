package com.dbg.mdm_offline_client

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform