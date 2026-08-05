package com.dbg.mdm_offline_client.domain.facts

internal fun String?.orNullIfBlank(): String? = this?.ifBlank { null }
