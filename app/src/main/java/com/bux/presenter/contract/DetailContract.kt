package com.bux.presenter.contract

import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import com.bux.domain.model.Product
import com.bux.presenter.contract.base.BasePresenter
import com.bux.presenter.contract.base.BaseView
import com.google.android.material.snackbar.BaseTransientBottomBar.Duration

/**
 * Contract between [com.bux.presenter.DetailPresenter] and [com.bux.activity.DetailActivity]
 */
interface DetailContract {

    interface Presenter : BasePresenter<View> {

        fun setProduct(product: Product)

    }

    interface View : BaseView<Presenter> {

        // Static (not updated)

        fun setDisplayName(name: String)

        fun setClosingPrice(price: String)

        // Dynamic

        fun setCurrentPrice(price: String)

        fun setArrowUp()

        fun setArrowDown()

        fun setPercentage(percentage: String)

        // Other (meta/errors)

        fun setLatency(latency: String, @ColorRes color: Int)

        fun setError(message: String, @Duration duration: Int)

        fun setError(@StringRes message: Int, @Duration duration: Int)

    }

}