package com.dbg.mdm_offline_client.api

import io.ktor.client.HttpClient
import kotlinx.serialization.json.Json

val MdmJson = Json {
    ignoreUnknownKeys = true
    encodeDefaults = true
    isLenient = true
}

expect fun createHttpClient(): HttpClient
