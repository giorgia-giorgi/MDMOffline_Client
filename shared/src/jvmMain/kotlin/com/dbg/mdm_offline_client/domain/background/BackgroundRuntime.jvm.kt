package com.dbg.mdm_offline_client.domain.background

import com.dbg.mdm_offline_client.network.local.startLocalServers
import kotlinx.coroutines.runBlocking
import java.util.concurrent.atomic.AtomicBoolean

/**
 * JVM/Windows parent: starts child background workers, then parks.
 */
actual object BackgroundRuntime {
    private val started = AtomicBoolean(false)
    private val running = AtomicBoolean(false)
    private var parentThread: Thread? = null

    actual fun start() {
        if (!started.compareAndSet(false, true)) return
        running.set(true)
        startLocalServers()
        parentThread = Thread(
            {
                runBlocking {
                    ServerEnrollment.ensureConnected()
                }
                if (running.get()) startChildWorkers()
                parkWhileRunning()
            },
            "mdm-background",
        ).apply {
            isDaemon = false
            start()
        }
    }

    actual fun stop() {
        running.set(false)
        parentThread?.interrupt()
        parentThread = null
        started.set(false)
    }

    private fun startChildWorkers() {
        Thread(
            {
                runBlocking {
                    StatusWorker.runForever(shouldContinue = { running.get() })
                }
            },
            "mdm-status",
        ).apply {
            isDaemon = false
            start()
        }
        Thread(
            {
                runBlocking {
                    UpdateInfoWorker.runForever(shouldContinue = { running.get() })
                }
            },
            "mdm-update-info",
        ).apply {
            isDaemon = false
            start()
        }
    }

    private fun parkWhileRunning() {
        while (running.get() && !Thread.currentThread().isInterrupted) {
            try {
                Thread.sleep(1_000L)
            } catch (_: InterruptedException) {
                break
            }
        }
    }
}
