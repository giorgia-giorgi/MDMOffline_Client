package com.dbg.mdm_offline_client.domain

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform