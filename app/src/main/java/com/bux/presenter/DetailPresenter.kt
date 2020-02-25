package com.bux.presenter

import android.os.Bundle
import com.bux.R
import com.bux.activity.SECURITY_ID
import com.bux.domain.model.PERCENTAGE_FORMAT
import com.bux.domain.model.Product
import com.bux.network.realtime.*
import com.bux.network.repository.ProductRepository
import com.bux.presenter.contract.DetailContract
import com.bux.util.Logger
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.tinder.scarlet.Message
import com.tinder.scarlet.WebSocket
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.koin.core.KoinComponent
import org.koin.core.inject
import kotlin.math.abs

/**
 * NOTE: I've observed that a plain [okhttp3.WebSocket] socket connection between 200-300ms on average.
 * Scarlet (v0.1.10) puts a huge overhead by (what I think) duplicating the flow.
 */
const val LATENCY_OK = 1_000
const val LATENCY_WARNING = 10_000

/**
 * TODO Describe class functionality.
 */
class DetailPresenter(private val view: DetailContract.View) : DetailContract.Presenter,
    KoinComponent {

    // TODO repository

    private val LOG_TAG = this::class.java.simpleName

    private val socket: SocketApi by inject()

    private val gson: Gson by inject()

    private val repo = ProductRepository()

    private val disposables = CompositeDisposable()

    private lateinit var product: Product

    private var subscriptions = Subscription()

    override fun start(bundle: Bundle?) {
        Logger.i(LOG_TAG, "start($bundle)")

        bundle?.let {
            it.getString(SECURITY_ID)?.let { securityId ->

                /**
                 *
                 */
                disposables.add(requestFromRest(securityId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { product ->
                        view.setDisplayName(product.displayName)

                        product.currentPrice?.let { price ->
                            view.setCurrentPrice(price.asCurrency())
                        }

                        product.closingPrice?.let { price ->
                            view.setClosingPrice(price.asCurrency())
                        }

                        setProduct(product)
                    })

                disposables.add(openSocket().subscribe { event ->
                    Logger.i(LOG_TAG, "onEvent($event)")

                    when (event) {
                        is WebSocket.Event.OnConnectionOpened<*> -> {
                            Logger.i(LOG_TAG, "socket connection opened.")
                        }
                        is WebSocket.Event.OnConnectionFailed -> {
                            Logger.e(LOG_TAG, "onFailure(${event.throwable.message})")

                            var message = "Error, check your connection"
                            event.throwable.localizedMessage?.let { errorMessage ->
                                message = errorMessage
                            }

                            view.setError(message, Snackbar.LENGTH_LONG)
                        }
                        is WebSocket.Event.OnMessageReceived -> {
                            subscriptions.subscribe(securityId)
                            socket.sendSubscription(subscriptions)
                        }
                    }
                })
            }
        }
    }

    private fun requestFromRest(id: String): Single<Product> {
        return repo.getSingle(id)
    }

    private fun mainStream(): Flowable<Quote> {
        return socket.mainStream()
            //.doOnEach { Logger.toString(LOG_TAG, "main stream", it) }
            .filter { it.type == MessageType.QUOTE }
            .map { it.getQuote()!! }
    }

    private fun openSocket(): Flowable<WebSocket.Event> {
        return socket.eventStream()
            //.doOnEach { Logger.toString(LOG_TAG, "event stream", it) }
            .filter filter@{ event ->

                if (event is WebSocket.Event.OnConnectionOpened<*> || event is WebSocket.Event.OnConnectionFailed) {
                    return@filter false
                }

                if (event is WebSocket.Event.OnMessageReceived) {
                    val textMessage = event.message as Message.Text
                    val container = gson.fromJson(textMessage.value, BuxMessage::class.java)
                    if (container.type == MessageType.CONNECTED) {
                        return@filter true
                    }
                }

                return@filter false
            }
    }

    override fun pause() {
        subscriptions.unsubscribe(product.securityId)
        socket.sendSubscription(subscriptions)
    }

    override fun setProduct(product: Product) {
        Logger.i(LOG_TAG, "setProduct($product)")
        this.product = product

        disposables.add(mainStream()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { message ->
                product.currentPrice?.let { current ->
                    view.setCurrentPrice(current.asCurrency(message.currentPrice))
                }

                // Current price and growth vs closing price
                val growth = this.product.getGrowth()
                if (growth > 0) {
                    view.setArrowUp()
                } else {
                    view.setArrowDown()
                }

                val growthFormatted = String.format(PERCENTAGE_FORMAT, abs(growth))
                view.setPercentage(growthFormatted)

                // latency check
                val latency = System.currentTimeMillis() - message.timeStamp
                var color: Int = R.color.jazz_green
                if (latency > LATENCY_OK) {
                    color = R.color.king_red
                }

                view.setLatency("$latency", color)

                // Warn user if the latency is very high
                if (latency > LATENCY_WARNING) {
                    view.setError(R.string.poor_connection, Snackbar.LENGTH_SHORT)
                }

            })
    }

    override fun stop() {
        disposables.clear()
    }
}