package com.dbg.mdm_offline_client

/**
 * Platform-specific key/value facts sent in `POST /update_info`.
 * Keys are free-form; the server merges sparsely (null value deletes a key).
 */
expect fun collectDeviceFacts(): Map<String, String?>
