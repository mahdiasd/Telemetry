package ir.manshourfanavari.library.network

import io.ktor.client.HttpClient
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.isSuccess
import ir.manshourfanavari.library.data.model.TelemetryEvent
import ir.manshourfanavari.library.data.model.TelemetryLog

internal class TelemetryApi(
    private val client: HttpClient,
    private val baseUrl: String,
    private val token: String
) {
    suspend fun sendLogs(logs: List<TelemetryLog>): Boolean {
        return try {
            val response = client.post("$baseUrl/logs") {
                header("Authorization", "Bearer $token")
                setBody(logs)
            }
            response.status.isSuccess()
        } catch (e: Exception) {
            false
        }
    }

    suspend fun sendEvents(events: List<TelemetryEvent>): Boolean {
        return try {
            val response = client.post("$baseUrl/events") {
                header("Authorization", "Bearer $token")
                setBody(events)
            }
            response.status.isSuccess()
        } catch (e: Exception) {
            false
        }
    }
}