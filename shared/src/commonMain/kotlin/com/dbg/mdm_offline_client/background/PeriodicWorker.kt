package com.dbg.mdm_offline_client.background

import kotlinx.coroutines.delay
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

/**
 * Runs [tick] immediately, then every [interval], until [shouldContinue] is false.
 */
suspend fun runPeriodicForever(
    interval: Duration,
    shouldContinue: () -> Boolean = { true },
    tick: suspend () -> Unit,
) {
    while (shouldContinue()) {
        tick()
        delay(interval)
    }
}

suspend fun runPeriodicForever(
    intervalMs: Long,
    shouldContinue: () -> Boolean = { true },
    tick: suspend () -> Unit,
) = runPeriodicForever(
    interval = intervalMs.milliseconds,
    shouldContinue = shouldContinue,
    tick = tick,
)
