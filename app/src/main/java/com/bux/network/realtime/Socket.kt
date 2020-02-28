package com.bux.network.realtime

import com.bux.BuildConfig
import com.tinder.scarlet.Scarlet
import com.tinder.scarlet.messageadapter.gson.GsonMessageAdapter
import com.tinder.scarlet.retry.ExponentialBackoffStrategy
import com.tinder.scarlet.streamadapter.rxjava2.RxJava2StreamAdapterFactory
import com.tinder.scarlet.websocket.okhttp.newWebSocketFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

/**
 * Socket for subscriptions to products.
 */
class Socket {

    private val LOG_TAG = this::class.java.simpleName

    fun create(): SocketApi {
        val interceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .addInterceptor intercept@{ chain ->
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

        val scarlet = Scarlet.Builder()
            .webSocketFactory(client.newWebSocketFactory("${BuildConfig.API_BASE}/subscriptions/me"))
            .addMessageAdapterFactory(GsonMessageAdapter.Factory())
            .addStreamAdapterFactory(RxJava2StreamAdapterFactory())
            .backoffStrategy(
                ExponentialBackoffStrategy(
                    initialDurationMillis = TimeUnit.SECONDS.toMillis(5),
                    maxDurationMillis = TimeUnit.MINUTES.toMillis(2)
                )
            )
            .build()

        return scarlet.create<SocketApi>(SocketApi::class.java)
    }

}