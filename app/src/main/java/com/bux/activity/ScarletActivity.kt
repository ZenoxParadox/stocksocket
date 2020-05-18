package com.bux.activity

import android.os.Bundle
import com.bux.BuildConfig
import com.bux.R
import com.bux.network.realtime.BuxMessage
import com.bux.network.realtime.MessageType
import com.bux.network.realtime.SocketApi
import com.bux.network.realtime.Subscription
import com.bux.util.Logger
import com.google.gson.Gson
import com.tinder.scarlet.Message
import com.tinder.scarlet.Scarlet
import com.tinder.scarlet.messageadapter.gson.GsonMessageAdapter
import com.tinder.scarlet.retry.ExponentialBackoffStrategy
import com.tinder.scarlet.streamadapter.rxjava2.RxJava2StreamAdapterFactory
import com.tinder.scarlet.websocket.ShutdownReason
import com.tinder.scarlet.websocket.WebSocketEvent
import com.tinder.scarlet.websocket.okhttp.OkHttpWebSocket
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_detail.*
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

/**
 * First activity where a list is shown to the user
 */
class ScarletActivity : BaseActivity() {

    private val LOG_TAG = this::class.java.simpleName

    private lateinit var socket: SocketApi

    private val gson = Gson()

    private val disposibles = CompositeDisposable()

    private var subscription = Subscription()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_debug)
        setSupportActionBar(toolbar)

        subscription.subscribe("sb26502")
        socket = getSocket()

        disposibles.add(socket.eventStream().subscribe { event ->
            Logger.toString(LOG_TAG, "event", event)

            if (event is WebSocketEvent.OnMessageReceived) {
                val textMessage = event.message as Message.Text
                val container = gson.fromJson(textMessage.value, BuxMessage::class.java)

                if (container.type == MessageType.CONNECTED) {
                    socket.sendSubscription(subscription)
                }

                // maybe it's a confirmation
                if (container.type == null) {
                    subscription = gson.fromJson(textMessage.value, Subscription::class.java)
                    Logger.toString(LOG_TAG, "subscription (ack)", subscription)
                }
            }
        })

        disposibles.add(socket.mainStream().subscribe { main ->
            Logger.i(LOG_TAG, "mainStream", main)

        })

    }


    private fun getSocket(): SocketApi {
        val client = getClient()

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

    override fun onPause() {
        super.onPause()
        disposibles.dispose()
    }

}
