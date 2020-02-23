package com.bux.presenter.contract

import com.bux.domain.model.Product
import com.bux.presenter.contract.base.BasePresenter
import com.bux.presenter.contract.base.BaseView

/**
 * TODO Describe class functionality.
 */
interface MainContract {

    interface Presenter : BasePresenter<View> {

        fun click(item: Product)

    }

    interface View : BaseView<Presenter> {

        fun showList(items: List<Product>)

        fun showDetail(item: Product)

    }

}