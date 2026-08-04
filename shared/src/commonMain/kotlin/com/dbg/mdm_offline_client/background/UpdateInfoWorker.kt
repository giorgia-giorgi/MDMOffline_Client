package com.dbg.mdm_offline_client.background

import com.dbg.mdm_offline_client.protocol.ProtocolConstants
import com.dbg.mdm_offline_client.settings.AppSettings
import com.dbg.mdm_offline_client.update.UpdateInfoReporter
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

/**
 * Child worker: on start ensures connectivity, then every 10 minutes posts `/update_info`.
 * On update failure, falls back to [ServerEnrollment.ensureConnected].
 */
object UpdateInfoWorker {
    suspend fun runForever(shouldContinue: () -> Boolean = { true }) {
        runForever(
            shouldContinue = shouldContinue,
            send = { UpdateInfoReporter.sendOnce() },
        )
    }

    suspend fun runForever(
        shouldContinue: () -> Boolean,
        send: suspend () -> Boolean,
    ) {
        while (shouldContinue()) {
            delay(ProtocolConstants.UPDATE_INFO_INTERVAL_MS.milliseconds)

            if (!AppSettings().tutorialCompleted) continue

            val ok = send()
            if (!ok) {
                ServerEnrollment.ensureConnected()
            }
        }
    }
}
