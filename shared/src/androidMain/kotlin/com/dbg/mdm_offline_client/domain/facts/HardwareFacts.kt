package com.dbg.mdm_offline_client.domain.facts

import android.os.Build

actual object HardwareFacts {
    fun manufacturer(): String? = Build.MANUFACTURER.orEmpty().orNullIfBlank()

    fun model(): String? = Build.MODEL.orEmpty().orNullIfBlank()

    actual fun toMap(): Map<String, String?> = buildMap {
        put(DeviceFactKeys.MANUFACTURER, manufacturer())
        put(DeviceFactKeys.MODEL, model())
    }
}
