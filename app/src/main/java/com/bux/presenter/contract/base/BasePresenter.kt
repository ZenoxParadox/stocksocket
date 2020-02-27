package com.bux.presenter.contract.base

import android.os.Bundle

/**
 * Base presenter
 */
interface BasePresenter<V : BaseView<*>> {

    /**
     * When you want the presenter to start with certain starting parameters, use the bundle to
     * pass such information.
     */
    fun start(bundle: Bundle? = null)

    /**
     * Equivalent to [android.app.Activity.onPause]
     */
    fun pause() {
        throw NotImplementedError("Called yet not implemented.")
    }

    /**
     * Equivalent to [android.app.Activity.onDestroy]
     */
    fun stop()

}