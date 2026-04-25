package ir.manshourfanavari.library.data.model

data class TelemetryConfig(
    val baseUrl: String,
    val token: String,
    val sendStrategy: SendStrategy = SendStrategy.RealTime,
)