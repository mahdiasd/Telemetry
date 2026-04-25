package ir.manshourfanavari.library.data.repository

import ir.manshourfanavari.library.data.model.TelemetryEvent
import ir.manshourfanavari.library.data.model.TelemetryLog
import ir.manshourfanavari.library.network.TelemetryApi

/**
 * Repository responsible for communicating with the telemetry backend.
 *
 * This layer ensures that network errors never crash the host application.
 */
internal class TelemetryRepository(
    private val api: TelemetryApi
) {

    /**
     * Sends telemetry events to the backend.
     *
     * @return true if the request was successful
     */
    suspend fun sendEvents(
        events: List<TelemetryEvent>
    ): Boolean {

        return runCatching {
            api.sendEvents(events)
        }.getOrElse {
            false
        }
    }

    /**
     * Sends telemetry logs to the backend.
     *
     * @return true if the request was successful
     */
    suspend fun sendLogs(
        logs: List<TelemetryLog>
    ): Boolean {

        return runCatching {
            api.sendLogs(logs)
        }.getOrElse {
            false
        }
    }
}