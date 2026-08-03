package com.dbg.mdm_offline_client.api

/**
 * iOS UDP broadcast discovery is not implemented in v1; use manual server entry.
 * HTTP `/status` + `/register` still work once a base URL is known.
 */
actual suspend fun discoverServerBaseUrl(): String? = null
