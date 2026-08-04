package com.dbg.mdm_offline_client.background

import com.dbg.mdm_offline_client.settings.AppSettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.time.Duration.Companion.seconds

actual object BackgroundRuntime {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val mutex = Mutex()
    private var started = false

    actual fun start() {
        scope.launch {
            mutex.withLock {
                if (started) return@withLock
                started = true
                ServerEnrollment.ensureConnected()
                startChildWorkers()
            }
        }
    }

    private fun startChildWorkers() {
        scope.launch {
            UpdateInfoWorker.runForever(shouldContinue = { isActive })
        }
    }
}
