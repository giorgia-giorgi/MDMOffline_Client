package com.dbg.mdm_offline_client.domain.background

import com.dbg.mdm_offline_client.network.protocol.ProtocolConstants
import com.dbg.mdm_offline_client.domain.update.StatusReporter

/**
 * Child worker: probes `GET /status` immediately, then every [ProtocolConstants.STATUS_INTERVAL_MS].
 * Request failures reconnect via [withEnsureConnected] inside [StatusReporter].
 */
object StatusWorker {
    suspend fun runForever(shouldContinue: () -> Boolean = { true }) {
        runForever(shouldContinue) { StatusReporter.checkOnce() }
    }

    suspend fun runForever(
        shouldContinue: () -> Boolean,
        check: suspend () -> Boolean,
    ) = runPeriodicForever(
        intervalMs = ProtocolConstants.STATUS_INTERVAL_MS,
        shouldContinue = shouldContinue,
        tick = { check() },
    )
}
