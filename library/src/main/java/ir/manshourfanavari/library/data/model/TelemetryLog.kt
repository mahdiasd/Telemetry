package ir.manshourfanavari.library.data.model

import android.annotation.SuppressLint
import ir.manshourfanavari.library.BuildConfig
import ir.manshourfanavari.library.data.utils.DeviceInfoProvider
import kotlinx.serialization.Serializable
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@SuppressLint("UnsafeOptInUsageError")
@OptIn(ExperimentalTime::class)
@Serializable
data class TelemetryLog(
    val environment: String = if (BuildConfig.DEBUG) EnvironmentType.Staging.name.lowercase() else EnvironmentType.Production.name.lowercase(),

    val timestamp: String = Clock.System.now().toString(),

    val context: Map<String, String>? = null,

    val level: LogLevel,

    val service: String,

    val appVersion: String? = null,

    val device: DeviceInfo = DeviceInfoProvider.get()
)
