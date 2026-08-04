package com.dbg.mdm_offline_client.background

import com.dbg.mdm_offline_client.settings.AppSettings
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.time.Duration.Companion.seconds

/**
 * JVM/Windows parent: starts child background workers, then parks.
 * Call [start] from `main` and forget.
 */
actual object BackgroundRuntime {
    private val started = AtomicBoolean(false)

    actual fun start() {
        if (!started.compareAndSet(false, true)) return
        Thread(
            {
                runBlocking {
                    // todo avoid polling here
                    while (!AppSettings().tutorialCompleted) {
                        delay(1.seconds)
                    }
                    ServerEnrollment.ensureConnected()
                }
                startChildWorkers()
                parkForever()
            },
            "mdm-background",
        ).apply {
            isDaemon = false
            start()
        }
    }

    private fun startChildWorkers() {
        Thread(
            { runBlocking { UpdateInfoWorker.runForever() } },
            "mdm-update-info",
        ).apply {
            isDaemon = false
            start()
        }
    }

    private fun parkForever() {
        while (!Thread.currentThread().isInterrupted) {
            try {
                Thread.sleep(Long.MAX_VALUE)
            } catch (_: InterruptedException) {
                break
            }
        }
    }
}
