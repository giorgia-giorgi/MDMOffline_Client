package com.dbg.mdm_offline_client.domain.background

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.os.PowerManager
import com.dbg.mdm_offline_client.AndroidContextHolder
import com.dbg.mdm_offline_client.network.local.startLocalServers
import com.dbg.mdm_offline_client.domain.update.StatusReporter
import com.dbg.mdm_offline_client.domain.update.UpdateInfoReporter
import com.dbg.mdm_offline_client.domain.update.requestIgnoreBatteryOptimizations
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * Android parent sticky service. Owns status and update-info child workers.
 */
class MdmBackgroundService : Service() {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var statusJob: Job? = null
    private var updateInfoJob: Job? = null
    private var wakeLock: PowerManager.WakeLock? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        AndroidContextHolder.init(applicationContext)
        requestIgnoreBatteryOptimizations(applicationContext)
        val pm = getSystemService(PowerManager::class.java)
        wakeLock = pm?.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "mdm:background")?.apply {
            setReferenceCounted(false)
        }

        startLocalServers()
        scope.launch {
            startChildWorkers()
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int = START_STICKY

    override fun onDestroy() {
        wakeLock?.let { lock ->
            if (lock.isHeld) lock.release()
        }
        scope.cancel()
        super.onDestroy()
    }

    private suspend fun startChildWorkers() {
        ServerEnrollment.ensureConnected()

        if (statusJob?.isActive != true) {
            statusJob = scope.launch {
                StatusWorker.runForever(
                    shouldContinue = { isActive },
                    check = { withWakeLock { StatusReporter.checkOnce() } },
                )
            }
        }
        if (updateInfoJob?.isActive != true) {
            updateInfoJob = scope.launch {
                UpdateInfoWorker.runForever(
                    shouldContinue = { isActive },
                    send = { withWakeLock { UpdateInfoReporter.sendOnce() } },
                )
            }
        }
    }

    private suspend fun withWakeLock(block: suspend () -> Boolean): Boolean {
        val lock = wakeLock
        return try {
            lock?.acquire(60_000L)
            block()
        } finally {
            if (lock?.isHeld == true) lock.release()
        }
    }

    companion object {
        fun start(context: Context) {
            context.startService(Intent(context, MdmBackgroundService::class.java))
        }
    }
}
