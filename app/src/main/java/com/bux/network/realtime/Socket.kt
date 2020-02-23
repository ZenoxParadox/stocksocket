package com.bux.network.realtime

import com.bux.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okhttp3.logging.HttpLoggingInterceptor

private const val token =
    "Bearer eyJhbGciOiJIUzI1NiJ9.eyJyZWZyZXNoYWJsZSI6ZmFsc2UsInN1YiI6ImJiMGNkYTJiLWExMGUtNGVkMy1hZDVhLTBmODJiNGMxNTJjNCIsImF1ZCI6ImJldGEuZ2V0YnV4LmNvbSIsInNjcCI6WyJhcHA6bG9naW4iLCJydGY6bG9naW4iXSwiZXhwIjoxODIwODQ5Mjc5LCJpYXQiOjE1MDU0ODkyNzksImp0aSI6ImI3MzlmYjgwLTM1NzUtNGIwMS04NzUxLTMzZDFhNGRjOGY5MiIsImNpZCI6Ijg0NzM2MjI5MzkifQ.M5oANIi2nBtSfIfhyUMqJnex-JYg6Sm92KPYaUL9GKg"
private const val accept = "nl-NL,en;q=0.8"

/**
 * TODO Describe class functionality.
 */
class Socket {

    private val LOG_TAG = this::class.java.simpleName

    private val client: OkHttpClient

    init {
        val interceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        client = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()
    }

    fun openConnection(listener: WebSocketListener): WebSocket {
        val authRequest: Request = Request.Builder().url("${BuildConfig.API_BASE}/subscriptions/me")
            .header("Authorization", token)
            .header("Accept-Language", accept)
            .build()

        return client.newWebSocket(authRequest, listener)
    }

}