package ir.manshourfanavari.library.data.utils

import ir.manshourfanavari.library.data.model.SendStrategy
import ir.manshourfanavari.library.data.model.TelemetryEvent
import ir.manshourfanavari.library.data.model.TelemetryLog
import ir.manshourfanavari.library.data.repository.TelemetryRepository
import ir.manshourfanavari.library.network.SharedPrefStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

internal class TelemetryDispatcher(
    private val repository: TelemetryRepository,
    private val storage: SharedPrefStorage,
    private val strategy: SendStrategy,
    private val scope: CoroutineScope
) {

    private val mutex = Mutex()

    fun enqueueEvent(
        event: TelemetryEvent
    ) {

        scope.launch {
            mutex.withLock {
                when (strategy) {
                    SendStrategy.RealTime -> {
                        val success = repository.sendEvents(listOf(event))

                        if (!success) {
                            storage.saveEvent(event)
                        }
                    }

                    is SendStrategy.Batch -> {
                        storage.saveEvent(event)
                        if (storage.eventsCount() >= strategy.count) {
                            flushEvents()
                        }
                    }
                }
            }
        }
    }

    fun enqueueLog(
        log: TelemetryLog
    ) {
        scope.launch {
            mutex.withLock {
                when (strategy) {
                    SendStrategy.RealTime -> {
                        val success = repository.sendLogs(
                            listOf(log)
                        )
                        if (!success) {
                            storage.saveLog(log)
                        }
                    }

                    is SendStrategy.Batch -> {
                        storage.saveLog(log)
                        if (storage.logsCount() >= strategy.count) {
                            flushLogs()
                        }
                    }
                }
            }
        }
    }

    private suspend fun flushEvents() {
        val events = storage.getEvents()
        if (events.isEmpty()) return
        val success = repository.sendEvents(events)
        if (success) {
            storage.clearEvents()
        }
    }

    private suspend fun flushLogs() {
        val logs = storage.getLogs()
        if (logs.isEmpty()) return
        val success = repository.sendLogs(logs)
        if (success) {
            storage.clearLogs()
        }
    }

    /**
     * Checks pending telemetry stored locally during SDK initialization.
     *
     * Behavior depends on the configured strategy:
     *
     * - RealTime: all stored telemetry will be sent immediately
     * - Batch: telemetry will be sent only if batch size is reached
     */
    suspend fun checkPending() {
        val events = storage.getEvents()
        val logs = storage.getLogs()

        when (strategy) {
            SendStrategy.RealTime -> {
                if (events.isNotEmpty()) {
                    val success = repository.sendEvents(events)
                    if (success) storage.clearEvents()
                }

                if (logs.isNotEmpty()) {
                    val success = repository.sendLogs(logs)
                    if (success) storage.clearLogs()
                }
            }

            is SendStrategy.Batch -> {
                if (events.size >= strategy.count) {
                    val success = repository.sendEvents(events)
                    if (success) storage.clearEvents()
                }

                if (logs.size >= strategy.count) {
                    val success = repository.sendLogs(logs)
                    if (success) storage.clearLogs()
                }
            }
        }
    }
}