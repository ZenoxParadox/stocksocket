package com.bux.presenter.contract

import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import com.bux.domain.model.Product
import com.bux.presenter.contract.base.BasePresenter
import com.bux.presenter.contract.base.BaseView
import com.google.android.material.snackbar.BaseTransientBottomBar.Duration

/**
 * TODO Describe class functionality.
 *
 * Created by Zenox on 21-2-2020 at 20:47.
 */
interface DetailContract {

    interface Presenter : BasePresenter<View> {

        fun requestUpdates(securityId: String)

        fun setProduct(product: Product)

    }

    interface View : BaseView<Presenter> {

        // TODO

        fun setDisplayName(name: String)

        fun setClosingPrice(price: String)

        // TODO

        fun setCurrentPrice(price: String)

        fun setArrowUp()

        fun setArrowDown()

        fun setPercentage(percentage: String)

        // TODO

        fun setLatency(latency: String, @ColorRes color: Int)

        fun setError(message: String, @Duration duration: Int)

        fun setError(@StringRes message: Int, @Duration duration: Int)

    }

}