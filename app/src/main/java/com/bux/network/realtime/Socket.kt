package com.bux.network.realtime

import com.bux.BuildConfig
import com.bux.util.Logger
import com.tinder.scarlet.Scarlet
import com.tinder.scarlet.messageadapter.gson.GsonMessageAdapter
import com.tinder.scarlet.retry.ExponentialBackoffStrategy
import com.tinder.scarlet.streamadapter.rxjava2.RxJava2StreamAdapterFactory
import com.tinder.scarlet.websocket.ShutdownReason
import com.tinder.scarlet.websocket.okhttp.OkHttpWebSocket
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

/**
 * Socket for subscriptions to products.
 */
class Socket {

    private val LOG_TAG = this::class.java.simpleName

    fun create(): SocketApi {
        Logger.i(LOG_TAG, "create")

        val interceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .callTimeout(10, TimeUnit.SECONDS)
            .readTimeout(5, TimeUnit.SECONDS)
            .writeTimeout(5, TimeUnit.SECONDS)
            .addInterceptor(interceptor)
            .addInterceptor intercept@{ chain ->
                Logger.i(LOG_TAG, "socket intercept")

                val original = chain.request()
                val builder = original.newBuilder()

                /**
                 * NOTE: this is where the authentication could go, for now this is a hardcoded solution.
                 */
                builder.addHeader("Authorization", "Bearer ${BuildConfig.TOKEN_SOCKET}")
                builder.addHeader("Accept-Language", "nl-NL,en;q=0.8")

                return@intercept chain.proceed(builder.build())
            }
            .build()

        val protocol = OkHttpWebSocket(
            client,
            OkHttpWebSocket.SimpleRequestFactory(
                { Request.Builder().url("${BuildConfig.API_BASE}/subscriptions/me").build() },
                { ShutdownReason.GRACEFUL }
            )
        )

        val configuration = Scarlet.Configuration(
            messageAdapterFactories = listOf(GsonMessageAdapter.Factory()),
            streamAdapterFactories = listOf(RxJava2StreamAdapterFactory()),
            backoffStrategy =                 ExponentialBackoffStrategy(
                initialDurationMillis = TimeUnit.SECONDS.toMillis(5),
                maxDurationMillis = TimeUnit.MINUTES.toMillis(2)
            )
        )

        val scarletInstance = Scarlet(protocol, configuration)
        return scarletInstance.create()
    }

}