package ir.manshourfanavari.library

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import io.ktor.client.HttpClient
import ir.manshourfanavari.library.data.model.DeviceInfo
import ir.manshourfanavari.library.data.model.EnvironmentType
import ir.manshourfanavari.library.data.model.LogLevel
import ir.manshourfanavari.library.data.model.SendStrategy
import ir.manshourfanavari.library.data.model.TelemetryConfig
import ir.manshourfanavari.library.data.model.TelemetryEvent
import ir.manshourfanavari.library.data.model.TelemetryEventType
import ir.manshourfanavari.library.data.model.TelemetryLog
import ir.manshourfanavari.library.data.repository.TelemetryRepository
import ir.manshourfanavari.library.data.utils.DeviceInfoProvider
import ir.manshourfanavari.library.data.utils.TelemetryDispatcher
import ir.manshourfanavari.library.network.SharedPrefStorage
import ir.manshourfanavari.library.network.TelemetryApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlin.time.ExperimentalTime


/**
 * Entry point for the Telemetry SDK.
 *
 * This object acts as a facade between the host application and the
 * internal telemetry system.
 *
 * Responsibilities:
 * - Initialize SDK components
 * - Dispatch telemetry events and logs
 * - Handle pending telemetry stored locally
 *
 * The SDK must be initialized using [init] before calling [track] or [log].
 */
object Telemetry {

    private lateinit var dispatcher: TelemetryDispatcher

    private val scope = CoroutineScope(
        SupervisorJob() + Dispatchers.IO
    )

    /**
     * Initializes the Telemetry SDK.
     *
     * This method must be called once during application startup.
     *
     * During initialization:
     * - Network API is created
     * - Local storage is prepared
     * - Dispatcher is configured
     * - Pending telemetry data is checked and flushed if needed
     *
     * @param context application context
     * @param config telemetry configuration
     */
    fun init(
        context: Context,
        config: TelemetryConfig
    ) {

        val api = createApi(config)
        val storage = createStorage(context)
        val repository = createRepository(api)

        dispatcher = createDispatcher(
            repository,
            storage,
            config
        )

        scope.launch {
            dispatcher.checkPending()
        }
    }

    private fun createApi(
        config: TelemetryConfig
    ): TelemetryApi {

        return TelemetryApi(
            client = HttpClientFactory().create(),
            token = config.token,
            baseUrl = config.baseUrl
        )
    }

    private fun createStorage(
        context: Context
    ): SharedPrefStorage {

        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        val prefs = EncryptedSharedPreferences.create(
            context,
            "telemetry_secure_storage",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

        return SharedPrefStorage(prefs)
    }

    private fun createRepository(
        api: TelemetryApi
    ): TelemetryRepository {

        return TelemetryRepository(api)
    }

    private fun createDispatcher(
        repository: TelemetryRepository,
        storage: SharedPrefStorage,
        config: TelemetryConfig
    ): TelemetryDispatcher {

        return TelemetryDispatcher(
            repository = repository,
            storage = storage,
            strategy = config.sendStrategy,
            scope = scope
        )
    }

    /**
     * Records a telemetry event.
     *
     * Depending on the configured [SendStrategy], the event will either:
     *
     * - be sent immediately to the backend
     * - be stored locally until batch conditions are met
     *
     * @param event telemetry event to record
     */
    @OptIn(ExperimentalTime::class)
    fun track(
        type: TelemetryEventType,
        title: String = "",
        context: Map<String, String>? = null,
        sessionId: String = "",
        appVersion: String? = null,
        environment: String = if (BuildConfig.DEBUG)
            EnvironmentType.Staging.name.lowercase()
        else
            EnvironmentType.Production.name.lowercase(),
        timestamp: String = Clock.System.now().toString(),
        device: DeviceInfo = DeviceInfoProvider.get()
    ) {

        if (!::dispatcher.isInitialized) return

        val event = TelemetryEvent(
            environment = environment,
            timestamp = timestamp,
            context = context,
            eventType = type,
            eventTitle = title,
            sessionId = sessionId,
            appVersion = appVersion,
            device = device
        )

        dispatcher.enqueueEvent(event)
    }

    @OptIn(ExperimentalTime::class)
    fun log(
        level: LogLevel,
        service: String,
        context: Map<String, String>? = null,
        appVersion: String? = null,
        environment: String = if (BuildConfig.DEBUG)
            EnvironmentType.Staging.name.lowercase()
        else
            EnvironmentType.Production.name.lowercase(),
        timestamp: String = Clock.System.now().toString(),
        device: DeviceInfo = DeviceInfoProvider.get()
    ) {

        if (!::dispatcher.isInitialized) return

        val log = TelemetryLog(
            environment = environment,
            timestamp = timestamp,
            context = context,
            level = level,
            service = service,
            appVersion = appVersion,
            device = device
        )

        dispatcher.enqueueLog(log)
    }
}