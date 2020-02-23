package com.bux.presenter

import com.bux.R
import com.bux.domain.*
import com.bux.domain.model.PERCENTAGE_FORMAT
import com.bux.domain.model.Product
import com.bux.network.realtime.ConnectionMessage
import com.bux.network.realtime.QuoteMessage
import com.bux.network.realtime.Socket
import com.bux.network.realtime.SocketEvent
import com.bux.presenter.contract.DetailContract
import com.bux.network.repository.ProductRepository
import com.bux.util.Logger
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import org.koin.core.KoinComponent
import org.koin.core.inject
import kotlin.math.abs

const val LATENCY_OK = 1_000
const val LATENCY_WARNING = 10_000

/**
 * TODO Describe class functionality.
 *
 * Created by Zenox on 21-2-2020 at 20:55.
 */
class DetailPresenter(private val view: DetailContract.View) : DetailContract.Presenter,
    KoinComponent {

    private val disposables = CompositeDisposable()

    // TODO repository

    private val LOG_TAG = this::class.java.simpleName

    private val gson: Gson by inject()
    private val socket: Socket by inject()

    private lateinit var connection: WebSocket

    private lateinit var product: Product

    override fun start() {
        connection = socket.openConnection(object : WebSocketListener() {

            override fun onOpen(webSocket: WebSocket, response: Response) {
                super.onOpen(webSocket, response)
                Logger.i(LOG_TAG, "opOpen($response)")
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                super.onClosed(webSocket, code, reason)
                Logger.i(LOG_TAG, "onClosed($code, $reason)")
            }

            override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
                super.onMessage(webSocket, bytes)
                throw NotImplementedError("Binary data not supported")
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                super.onMessage(webSocket, text)

                val container = gson.fromJson(text, MessageContainer::class.java)
                //Logger.toString(LOG_TAG, "container", container)

                when (container.type) {
                    MessageType.CONNECTED -> {
                        val message = gson.fromJson(container.body, ConnectionMessage::class.java)
                        Logger.toString(LOG_TAG, "message", message)
                    }

                    MessageType.QUOTE -> {
                        val message = gson.fromJson(container.body, QuoteMessage::class.java)

                        // latency check
                        val latency = System.currentTimeMillis() - message.timeStamp
                        var color: Int = R.color.jazz_green
                        if (latency > LATENCY_OK) {
                            color = R.color.king_red
                        }

                        // Warn user if the latency is very high
                        val latencyMessage: Int? = if (latency > LATENCY_WARNING) {
                            R.string.poor_connection
                        } else {
                            null
                        }

                        // Current price and growth vs closing price
                        var growth = 0.0
                        var currentPrice: String? = null

                        if (this@DetailPresenter::product.isInitialized) {
                            product.currentPrice?.amount = message.currentPrice

                            growth = product.getGrowth()

                            product.currentPrice?.let { current ->
                                currentPrice = current.asCurrency(message.currentPrice)
                            }
                        }

                        val growthFormatted = String.format(PERCENTAGE_FORMAT, abs(growth))

                        AndroidSchedulers.mainThread().scheduleDirect {
                            view.setLatency("$latency", color)

                            latencyMessage?.let {
                                view.setError(it, Snackbar.LENGTH_SHORT)
                            }

                            currentPrice?.let {
                                view.setCurrentPrice(it)
                            }

                            if (growth > 0) {
                                view.setArrowUp()
                            } else {
                                view.setArrowDown()
                            }

                            view.setPercentage(growthFormatted)
                        }

                    }
                    else -> {
                        // default case
                    }
                }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                super.onFailure(webSocket, t, response)
                Logger.e(LOG_TAG, "onFailure($response)")

                var message = "Error, check your connection"
                t.localizedMessage?.let {
                    message = it
                }

                view.setError(message, Snackbar.LENGTH_LONG)

                webSocket.close(1001, "Connection issue")
            }

        })
    }

    override fun pause() {
        val event = SocketEvent()
        event.unsubscribe(product.securityId)

        val data = event.toJson(gson)
        Logger.toString(LOG_TAG, "data", data)

        connection.send(data)
    }

    override fun setProduct(product: Product) {
        this.product = product
    }

    override fun requestUpdates(securityId: String) {
        val event = SocketEvent()
        event.subscribe(securityId)

        val data = event.toJson(gson)
        Logger.toString(LOG_TAG, "data", data)

        connection.send(data)

        // Also get the xx
        val repo = ProductRepository()
        disposables.add(
            repo.getSingle(securityId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { product ->
                    view.setDisplayName(product.displayName)

                    product.currentPrice?.let {
                        view.setCurrentPrice(it.asCurrency())
                    }

                    product.closingPrice?.let {
                        view.setClosingPrice(it.asCurrency())
                    }

                    setProduct(product)
                }
        )
    }


    override fun stop() {
        disposables.clear()
        connection.close(1000, "purpose fulfilled")
    }
}