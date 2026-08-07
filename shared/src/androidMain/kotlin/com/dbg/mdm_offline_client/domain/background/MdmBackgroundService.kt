package com.dbg.mdm_offline_client.domain.background

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import com.dbg.mdm_offline_client.AndroidContextHolder
import com.dbg.mdm_offline_client.domain.update.requestIgnoreBatteryOptimizations
import com.dbg.mdm_offline_client.network.local.startLocalServers
import com.dbg.mdm_offline_client.shared.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * Always-on MDM agent: local UDP/HTTP servers plus status and update-info workers.
 * Runs as a foreground service so Android allows continuous background execution.
 */
class MdmBackgroundService : Service() {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var statusJob: Job? = null
    private var updateInfoJob: Job? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        AndroidContextHolder.init(applicationContext)
        startAsForeground()
        requestIgnoreBatteryOptimizations(applicationContext)

        startLocalServers()
        scope.launch {
            startChildWorkers()
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int = START_STICKY

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }

    private fun startAsForeground() {
        ensureNotificationChannel()
        val notification = buildNotification()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(
                NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE,
            )
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }
    }

    private fun ensureNotificationChannel() {
        val manager = getSystemService(NotificationManager::class.java) ?: return
        val channel = NotificationChannel(
            CHANNEL_ID,
            getString(R.string.mdm_fg_channel_name),
            NotificationManager.IMPORTANCE_LOW,
        ).apply {
            description = getString(R.string.mdm_fg_channel_description)
            setShowBadge(false)
        }
        manager.createNotificationChannel(channel)
    }

    private fun buildNotification(): Notification {
        val launchIntent = packageManager.getLaunchIntentForPackage(packageName)
        val contentIntent = launchIntent?.let {
            PendingIntent.getActivity(
                this,
                0,
                it,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )
        }
        return Notification.Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.mdm_fg_notification_title))
            .setContentText(getString(R.string.mdm_fg_notification_text))
            .setSmallIcon(R.drawable.ic_mdm_notification)
            .setContentIntent(contentIntent)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .build()
    }

    private suspend fun startChildWorkers() {
        ServerEnrollment.ensureConnected()

        if (statusJob?.isActive != true) {
            statusJob = scope.launch {
                StatusWorker.runForever(
                    shouldContinue = { isActive },
                )
            }
        }
        if (updateInfoJob?.isActive != true) {
            updateInfoJob = scope.launch {
                UpdateInfoWorker.runForever(
                    shouldContinue = { isActive },
                )
            }
        }
    }

    companion object {
        private const val CHANNEL_ID = "mdm_foreground"
        private const val NOTIFICATION_ID = 1001

        fun start(context: Context) {
            val intent = Intent(context, MdmBackgroundService::class.java)
            context.startForegroundService(intent)
        }

        fun stop(context: Context) {
            context.stopService(Intent(context, MdmBackgroundService::class.java))
        }
    }
}
