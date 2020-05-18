package com.bux.activity

import android.os.Bundle
import com.bux.BuildConfig
import com.bux.R
import com.bux.network.realtime.BuxMessage
import com.bux.network.realtime.MessageType
import com.bux.network.realtime.Quote
import com.bux.network.realtime.Subscription
import com.bux.util.Logger
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_debug.*
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import okio.ByteString

/**
 * First activity where a list is shown to the user
 */
class PlainActivity : BaseActivity() {

    private val LOG_TAG = this::class.java.simpleName

    private lateinit var socket: WebSocket

    private val gson = Gson()

    private var subscription = Subscription()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_debug)
        setSupportActionBar(toolbar)

        subscription.subscribe("sb26502")
        socket = getSocket()

    }


    private fun getSocket(): WebSocket {
        val client = getClient()

        // Auth
        val builder = Request.Builder()
        builder.url("${BuildConfig.API_BASE}/subscriptions/me")

        return client.newWebSocket(builder.build(), object : WebSocketListener() {

            override fun onOpen(webSocket: WebSocket, response: Response) {
                super.onOpen(webSocket, response)
                Logger.i(LOG_TAG, "onOpen()")
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                super.onFailure(webSocket, t, response)
                Logger.i(LOG_TAG, "onFailure(${t.message})")
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                super.onMessage(webSocket, text)
                Logger.i(LOG_TAG, "onMessage($text)")

                val container = gson.fromJson(text, BuxMessage::class.java)

                if (container.type == null) {
                    subscription = gson.fromJson(text, Subscription::class.java)
                    Logger.toString(LOG_TAG, "(ACK) subscription", subscription)
                } else {
                    when (container.type) {
                        MessageType.CONNECTED -> {
                            val subscribe = gson.toJson(subscription)
                            socket.send(subscribe)
                        }
                        MessageType.QUOTE -> {
                            onMessage(container.getQuote())
                        }
                    }
                }
            }

            override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
                super.onMessage(webSocket, bytes)
                throw NotImplementedError("bytes not supported")
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                super.onClosing(webSocket, code, reason)
                Logger.w(LOG_TAG, "onClosing($reason)")
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                super.onClosed(webSocket, code, reason)
                Logger.e(LOG_TAG, "onClosed($reason)")
            }

        })
    }

    private fun onMessage(quote: Quote) {
        Logger.i(LOG_TAG, "onMessage($quote)")
    }

    private fun getClient(): OkHttpClient {

        val interceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
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
    }

    override fun onStop() {
        super.onStop()
        socket.close(1001, "done.")
    }

}
