package com.bux.presenter.contract.base

import android.os.Bundle

/**
 * TODO Describe class functionality.
 */
interface BasePresenter<V : BaseView<*>> {

    fun start(bundle: Bundle? = null)

    fun pause() {
        throw NotImplementedError("Called yet not implemented.")
    }

    fun stop()

}