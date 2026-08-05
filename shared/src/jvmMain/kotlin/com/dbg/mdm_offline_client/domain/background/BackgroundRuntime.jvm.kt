package com.dbg.mdm_offline_client.domain.background

import com.dbg.mdm_offline_client.network.local.startLocalServers
import kotlinx.coroutines.runBlocking
import java.util.concurrent.atomic.AtomicBoolean

/**
 * JVM/Windows parent: starts child background workers, then parks.
 * Call [start] from `main` and forget.
 */
actual object BackgroundRuntime {
    private val started = AtomicBoolean(false)

    actual fun start() {
        if (!started.compareAndSet(false, true)) return
        startLocalServers()
        Thread(
            {
                runBlocking {
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
            { runBlocking { StatusWorker.runForever() } },
            "mdm-status",
        ).apply {
            isDaemon = false
            start()
        }
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
