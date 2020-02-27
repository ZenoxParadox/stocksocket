package com.bux.presenter

import android.os.Bundle
import com.bux.BaseTest
import com.bux.Bux
import com.bux.activity.SECURITY_ID
import com.bux.domain.model.Price
import com.bux.domain.model.Product
import com.bux.network.realtime.BuxMessage
import com.bux.network.realtime.MessageType
import com.bux.network.realtime.SocketApi
import com.bux.network.repository.ProductRepository
import com.bux.presenter.contract.DetailContract
import com.google.gson.JsonParser
import com.tinder.scarlet.Message
import com.tinder.scarlet.WebSocket
import io.mockk.*
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Single
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.test.mock.declareMock
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.*

/**
 * Tests for [DetailPresenter]
 */
@RunWith(RobolectricTestRunner::class)
@Config(application = Bux::class, sdk = [28])
class DetailPresenterTest : BaseTest() {

    private val view = spyk<DetailContract.View>()

    private val testProduct = Product(
        displayName = "example",
        securityId = "sb26502",
        symbol = "example",
        closingPrice = Price(currency = "EUR", decimals = 3, amount = 1.123),
        currentPrice = Price(currency = "EUR", decimals = 3, amount = 1.456)
    )

    @Test
    fun `a1 - should not run without bundle`() {
        val presenter = DetailPresenter(view)
        presenter.start()

        verify(exactly = 0) {
            view.setDisplayName(any())
        }
    }

    @Test
    fun `a2 - should show the name of the product`() {
        val presenter = DetailPresenter(view)

        declareMock<ProductRepository> {
            every {
                getSingle(testProduct.securityId)
            } answers {
                Single.just(testProduct)
            }
        }

        Locale.setDefault(Locale.US)

        val bundle = Bundle()
        bundle.putString(SECURITY_ID, testProduct.securityId)
        presenter.start(bundle)

        verify(exactly = 1) {
            view.setDisplayName("example")
            view.setCurrentPrice("EUR1.456")
        }
    }

    @Test
    fun `a3 - should show the name of the product`() {
        val presenter = spyk(DetailPresenter(view), recordPrivateCalls = true)

        /**
         * Note the type (variable t) is "connected.connected"
         */
        val BODY_CONNECTED = "{ \"body\": { \"userId\": \"bb0cda2b-a10e-4ed3-ad5a-0f82b4c152c4\", \"sessionId\":\"b1c30e28-a2b3-4b1b-8bcb-f9be85a50c17\", \"time\": 1582285348913 }, \"t\": \"connect.connected\" }"

        /**
         * Note the securityId is "sb26502" (=same as the testProduct)
         */
        val BODY_QUOTE = "{ \"securityId\": \"sb26502\", \"currentPrice\": 1.29079, \"timeStamp\": 1582231088710 }"

        declareMock<ProductRepository> {
            every {
                getSingle(testProduct.securityId)
            } answers {
                Single.just(testProduct)
            }
        }

        declareMock<SocketApi> {
            every {
                eventStream()
            } answers {
                val firstEvent = WebSocket.Event.OnMessageReceived(Message.Text(BODY_CONNECTED))
                Flowable.just(firstEvent)
            }

            every {
                mainStream()
            } answers {
                val element = JsonParser().parse(BODY_QUOTE)
                val message = BuxMessage(type = MessageType.QUOTE, body = element)

                Flowable.create({ emitter ->
                    emitter.onNext(message)
                }, BackpressureStrategy.LATEST)
            }

            every {
                sendSubscription(any())
            } just Runs
        }

        Locale.setDefault(Locale.US)

        val bundle = Bundle()
        bundle.putString(SECURITY_ID, testProduct.securityId)
        presenter.start(bundle)

        verifyOrder {
            presenter.start(bundle)

            // basic rest calls
            presenter["requestFromRest"]
            view.setDisplayName(any())
            view.setCurrentPrice(any())
            view.setClosingPrice(any())
            presenter.setProduct(testProduct)

            // socket calls
            presenter["getSocket"]
            view.setCurrentPrice("EUR1.291")
            view.setArrowUp()
            // view.setLatency(any(), any()) <-- ignore hardcoded time difference
            view.setPercentage("29.65%")
            // view.setError(any(), any()) <-- ignore latency error
        }
    }

}
