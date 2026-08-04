package com.dbg.mdm_offline_client.background

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.os.PowerManager
import com.dbg.mdm_offline_client.AndroidContextHolder
import com.dbg.mdm_offline_client.settings.AppSettings
import com.dbg.mdm_offline_client.update.UpdateInfoReporter
import com.dbg.mdm_offline_client.update.requestIgnoreBatteryOptimizations
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

/**
 * Android parent sticky service. Owns the update-info child worker.
 */
class MdmBackgroundService : Service() {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
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

        scope.launch {
            ServerEnrollment.ensureConnected()
        }

        startChildWorkers()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int = START_STICKY

    override fun onDestroy() {
        wakeLock?.let { lock ->
            if (lock.isHeld) lock.release()
        }
        scope.cancel()
        super.onDestroy()
    }

    private fun startChildWorkers() {
        if (updateInfoJob?.isActive == true) return
        updateInfoJob = scope.launch {
            UpdateInfoWorker.runForever(
                shouldContinue = { isActive },
                send = { sendUpdateWithWakeLock() },
            )
        }
    }

    private suspend fun sendUpdateWithWakeLock(): Boolean {
        val lock = wakeLock
        return try {
            lock?.acquire(60_000L)
            UpdateInfoReporter.sendOnce()
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
