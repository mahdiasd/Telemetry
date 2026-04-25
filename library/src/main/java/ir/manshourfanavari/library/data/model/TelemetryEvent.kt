package ir.manshourfanavari.library.data.model

import android.annotation.SuppressLint
import android.os.Environment
import androidx.core.os.BuildCompat
import ir.manshourfanavari.library.BuildConfig
import ir.manshourfanavari.library.data.utils.DeviceInfoProvider
import kotlinx.serialization.Serializable
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class TelemetryEvent(

    val environment: String = if (BuildConfig.DEBUG)
        EnvironmentType.Staging.name.lowercase()
    else
        EnvironmentType.Production.name.lowercase(),

    val timestamp: String = Clock.System.now().toString(),

    val context: Map<String, String>? = null,

    val eventType: TelemetryEventType,

    val eventTitle: String = "",

    val sessionId: String = "",

    val appVersion: String? = null,

    val device: DeviceInfo = DeviceInfoProvider.get()
)