package ir.manshourfanavari.library.data.model

import android.annotation.SuppressLint
import kotlinx.serialization.Serializable

/**
 * Represents the type of a telemetry event.
 *
 * This is implemented as a value class to provide type safety while still allowing
 * dynamic event types to be defined by SDK consumers.
 *
 * The SDK includes a few common predefined types such as [Visit] and [Click],
 * but applications can create their own custom event types when needed.
 *
 * Example (built-in types):
 * ```
 * TelemetryEventType.Visit
 * TelemetryEventType.Click
 * ```
 *
 * Example (custom type defined by the app):
 * ```
 * val Purchase = TelemetryEventType("purchase")
 * val PaymentFailed = TelemetryEventType("payment_failed")
 * ```
 *
 * Custom types make it possible to extend the telemetry system without requiring
 * changes or updates to the SDK itself.
 */
@SuppressLint("UnsafeOptInUsageError")
@JvmInline
@Serializable
value class TelemetryEventType(
    val value: String
) {

    companion object {

        val Visit = TelemetryEventType("visit")

        val Click = TelemetryEventType("click")

    }
}