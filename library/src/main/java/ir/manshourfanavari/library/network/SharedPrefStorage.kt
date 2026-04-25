package ir.manshourfanavari.library.network

import android.content.SharedPreferences
import ir.manshourfanavari.library.data.model.TelemetryLog
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.collections.toMutableList
import androidx.core.content.edit
import ir.manshourfanavari.library.data.model.TelemetryEvent
internal class SharedPrefStorage(
    private val prefs: SharedPreferences
) {

    private val logsKey = "telemetry_logs"
    private val eventsKey = "telemetry_events"

    fun saveLog(value: TelemetryLog) {
        val current = getLogs().toMutableList()
        current.add(value)

        prefs.edit {
            putString(logsKey, Json.encodeToString(current))
        }
    }

    fun saveEvent(value: TelemetryEvent) {
        val current = getEvents().toMutableList()
        current.add(value)

        prefs.edit {
            putString(eventsKey, Json.encodeToString(current))
        }
    }

    fun getLogs(): List<TelemetryLog> {
        val json = prefs.getString(logsKey, null) ?: return emptyList()

        return Json.decodeFromString(json)
    }

    fun getEvents(): List<TelemetryEvent> {
        val json = prefs.getString(eventsKey, null) ?: return emptyList()

        return Json.decodeFromString(json)
    }

    fun logsCount(): Int = getLogs().size

    fun eventsCount(): Int = getEvents().size

    fun clearLogs() {
        prefs.edit { remove(logsKey) }
    }

    fun clearEvents() {
        prefs.edit { remove(eventsKey) }
    }
}