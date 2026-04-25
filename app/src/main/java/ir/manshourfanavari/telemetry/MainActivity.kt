package ir.manshourfanavari.telemetry

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ir.manshourfanavari.library.Telemetry
import ir.manshourfanavari.library.data.model.LogLevel
import ir.manshourfanavari.library.data.model.SendStrategy
import ir.manshourfanavari.library.data.model.TelemetryConfig
import ir.manshourfanavari.library.data.model.TelemetryEvent
import ir.manshourfanavari.library.data.model.TelemetryEventType
import ir.manshourfanavari.library.data.model.TelemetryLog
import ir.manshourfanavari.telemetry.ui.theme.TelemetryTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Telemetry.init(
            context = applicationContext,
            config = TelemetryConfig(
                baseUrl = "https://your-api.com",
                token = "test-token",
                sendStrategy = SendStrategy.Batch(5)
            )
        )

        setContent {
            TelemetryTestScreen()
        }
    }
}

@Composable
fun TelemetryTestScreen() {
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Button(
            onClick = {
                Telemetry.track(
                    type = TelemetryEventType.Visit,
                    title = "home_screen"
                )
            }
        ) {
            Text("Send Visit Event")
        }

        Button(
            onClick = {
                Telemetry.track(
                    type = TelemetryEventType.Click,
                    title = "login_button",
                    context = mapOf(
                        "screen" to "login",
                        "component" to "button"
                    )
                )
            }
        ) {
            Text("Send Click Event")
        }

        Button(
            onClick = {

                val purchaseEvent = TelemetryEventType("purchase")

                Telemetry.track(
                    type = purchaseEvent,
                    title = "purchase_completed",
                    context = mapOf(
                        "price" to "12.99",
                        "currency" to "USD"
                    )
                )
            }
        ) {
            Text("Send Custom Event")
        }

        Button(
            onClick = {

                Telemetry.log(
                    level = LogLevel.Info,
                    service = "auth-service",
                    context = mapOf(
                        "action" to "login_attempt"
                    )
                )
            }
        ) {
            Text("Send Info Log")
        }

        Button(
            onClick = {
                Telemetry.log(
                    level = LogLevel.Error,
                    service = "payment-service",
                    context = mapOf(
                        "error" to "payment_failed"
                    )
                )
            }
        ) {
            Text("Send Error Log")
        }

        Button(
            onClick = {

                repeat(10) {

                    Telemetry.track(
                        type = TelemetryEventType.Click,
                        title = "stress_click_$it"
                    )
                }

            }
        ) {
            Text("Batch Event Test (10)")
        }

        Button(
            onClick = {
                scope.launch {
                    repeat(5) {
                        Telemetry.track(
                            type = TelemetryEventType.Visit,
                            title = "delayed_event_$it"
                        )

                        delay(800)

                        Telemetry.log(
                            level = LogLevel.Error,
                            service = "ui",
                            context = mapOf(
                                "step" to it.toString()
                            )
                        )

                        delay(800)
                    }
                }

            }
        ) {
            Text("Full Scenario Test")
        }
    }
}