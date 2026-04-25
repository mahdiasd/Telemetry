package ir.manshourfanavari.library

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json

internal class HttpClientFactory {

    fun create(): HttpClient {

        return HttpClient(OkHttp) {

            install(ContentNegotiation.Plugin) {
                json()
            }

            engine {
                config {
                    retryOnConnectionFailure(true)
                }
            }

            expectSuccess = false

            defaultRequest {
                contentType(ContentType.Application.Json)
            }
        }
    }
}