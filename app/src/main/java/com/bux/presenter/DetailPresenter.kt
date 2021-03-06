package com.bux.presenter

import android.os.Bundle
import com.bux.R
import com.bux.activity.SECURITY_ID
import com.bux.domain.model.PERCENTAGE_FORMAT
import com.bux.domain.model.Product
import com.bux.network.realtime.*
import com.bux.network.repository.ProductRepository
import com.bux.network.rest.BuxException
import com.bux.network.rest.ErrorType
import com.bux.network.rest.findOrThrow
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
import io.reactivex.exceptions.CompositeException
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
 * Presenter for product detail [com.bux.activity.DetailActivity]
 */
class DetailPresenter(private val view: DetailContract.View) : DetailContract.Presenter,
    KoinComponent {

    private val LOG_TAG = this::class.java.simpleName

    private val socket: SocketApi by inject()

    private val gson: Gson by inject()

    private val repo: ProductRepository by inject()

    private val disposables = CompositeDisposable()

    private lateinit var product: Product

    private var subscriptions = Subscription()

    override fun start(bundle: Bundle?) {
        Logger.i(LOG_TAG, "start($bundle)")

        bundle?.getString(SECURITY_ID)?.let { securityId ->
            subscriptions.subscribe(securityId)

            disposables.add(
                requestFromRest(securityId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ product ->
                        view.setDisplayName(product.displayName)

                        product.currentPrice?.let { price ->
                            view.setCurrentPrice(price.asCurrency())
                        }

                        product.closingPrice?.let { price ->
                            view.setClosingPrice(price.asCurrency())
                        }

                        setProduct(product)
                    }, { error ->
                        Logger.e(LOG_TAG, "error: ${error.message}")

                        /**
                         * Note: It's possible to continue throwing this to make it land in a
                         * generic section. This is to indicate that we MIGHT want to show a user
                         * message here based on one [com.bux.network.rest.ErrorType] or finish
                         * the navigation stack on another (for example [com.bux.network.rest.ErrorType.EXPIRED].
                         * Assuming we cannot refresh the token).
                         */
                        if (error is CompositeException) {
                            val buxError = findOrThrow<BuxException>(error)
                            Logger.e(LOG_TAG, "BuxError: ${buxError.hint}. (${buxError.code})")

                            when (buxError.code) {
                                ErrorType.EXPIRED -> {
                                    view.setError(buxError.code.text, Snackbar.LENGTH_LONG)
                                }
                                else -> {
                                    throw buxError
                                }
                            }

                            return@subscribe
                        }

                        Logger.w(LOG_TAG, "Could not find out what to do.")
                        throw error
                    })
            )

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
                        socket.sendSubscription(subscriptions)
                    }
                }
            })
        }
    }

    private fun requestFromRest(id: String): Single<Product> {
        return repo.getSingle(id)
    }

    private fun mainStream(): Flowable<Quote> {
        return socket.mainStream()
            .filter { it.type == MessageType.QUOTE }
            .map { it.getQuote()!! }
    }

    private fun openSocket(): Flowable<WebSocket.Event> {
        return socket.eventStream()
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
        if (this::product.isInitialized) {
            subscriptions.unsubscribe(product.securityId)
        }

        socket.sendSubscription(subscriptions)
    }

    override fun setProduct(product: Product) {
        this.product = product

        disposables.add(mainStream()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { message ->
                this.product.currentPrice?.amount = message.currentPrice

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