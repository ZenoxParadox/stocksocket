package com.bux.presenter

import com.bux.BaseTest
import com.bux.Bux
import com.bux.domain.model.Product
import com.bux.network.repository.ProductRepository
import com.bux.presenter.contract.MainContract
import io.mockk.every
import io.mockk.spyk
import io.mockk.verify
import io.reactivex.Single
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.test.mock.declareMock
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Tests for [MainPresenter]
 */
@RunWith(RobolectricTestRunner::class)
@Config(application = Bux::class, sdk = [28])
class MainPresenterTest : BaseTest() {

    private val view = spyk<MainContract.View>(recordPrivateCalls = true)

    private lateinit var presenter: MainPresenter

    @Before
    fun setup(){
        presenter = MainPresenter(view)
    }

    @Test
    fun `a1 - should call showList once`() {

        declareMock<ProductRepository>{
            every {
                getAll()
            } returns Single.just(emptyList())
        }

        presenter.start()

        verify(exactly = 1) {
            view.showList(any())
        }
    }

    @Test
    fun `a2 - should go to detail screen`() {
        val product = Product(
            securityId = "abc",
            displayName = "example",
            symbol = "$",
            currentPrice = null,
            closingPrice = null
        )

        presenter.start()
        presenter.click(product)

        verify(exactly = 1) {
            view.showDetail(product)
        }
    }

}