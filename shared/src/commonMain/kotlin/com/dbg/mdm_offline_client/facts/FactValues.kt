package com.dbg.mdm_offline_client.facts

internal fun String?.orNullIfBlank(): String? = this?.ifBlank { null }
