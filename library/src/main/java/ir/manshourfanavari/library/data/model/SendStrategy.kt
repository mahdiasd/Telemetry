package ir.manshourfanavari.library.data.model


/**
 * Defines how telemetry data should be delivered to the server.
 *
 * Two delivery strategies are supported:
 *
 * - [RealTime] : Each event/log is sent immediately.
 * - [Batch] : Data is stored locally and sent once the specified count is reached.
 *
 * Batch mode improves network efficiency by reducing the number of requests.
 */
sealed class SendStrategy {

    /**
     * Sends telemetry data immediately after it is recorded.
     */
    data object RealTime : SendStrategy()

    /**
     * Stores telemetry locally and sends it once the number
     * of stored items reaches [count].
     *
     * @param count minimum number of items required before sending
     */
    data class Batch(
        val count: Int
    ) : SendStrategy()
}