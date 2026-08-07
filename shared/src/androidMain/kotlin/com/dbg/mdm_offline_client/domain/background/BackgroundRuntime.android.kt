package com.dbg.mdm_offline_client.domain.background

import com.dbg.mdm_offline_client.AndroidContextHolder

actual object BackgroundRuntime {
    actual fun start() {
        MdmBackgroundService.start(AndroidContextHolder.requireContext())
    }

    actual fun stop() {
        MdmBackgroundService.stop(AndroidContextHolder.requireContext())
    }
}
