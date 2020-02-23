package com.bux.presenter.contract.base

/**
 * TODO Describe class functionality.
 *
 * Created by Zenox on 21-2-2020 at 20:34.
 */
interface BasePresenter<V : BaseView<*>> {

    fun start()

    fun pause() {
        throw NotImplementedError("Called yet not implemented.")
    }

    fun stop()

}