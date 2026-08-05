package com.dbg.mdm_offline_client.domain.background

import com.dbg.mdm_offline_client.network.protocol.ProtocolConstants
import com.dbg.mdm_offline_client.domain.update.UpdateInfoReporter

/**
 * Child worker: posts `/update_info` immediately, then every [ProtocolConstants.UPDATE_INFO_INTERVAL_MS].
 * Request failures reconnect via [withEnsureConnected] inside [UpdateInfoReporter].
 */
object UpdateInfoWorker {
    suspend fun runForever(shouldContinue: () -> Boolean = { true }) {
        runForever(shouldContinue) { UpdateInfoReporter.sendOnce() }
    }

    suspend fun runForever(
        shouldContinue: () -> Boolean,
        send: suspend () -> Boolean,
    ) = runPeriodicForever(
        intervalMs = ProtocolConstants.UPDATE_INFO_INTERVAL_MS,
        shouldContinue = shouldContinue,
        tick = { send() },
    )
}
