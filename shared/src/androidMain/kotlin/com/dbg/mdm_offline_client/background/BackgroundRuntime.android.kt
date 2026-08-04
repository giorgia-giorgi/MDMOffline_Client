package com.dbg.mdm_offline_client.background

import com.dbg.mdm_offline_client.AndroidContextHolder

actual object BackgroundRuntime {
    actual fun start() {
        MdmBackgroundService.start(AndroidContextHolder.requireContext())
    }
}
