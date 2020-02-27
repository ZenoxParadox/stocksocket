package com.bux.presenter

import android.os.Bundle
import com.bux.domain.model.Product
import com.bux.network.repository.ProductRepository
import com.bux.presenter.contract.MainContract
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import org.koin.core.KoinComponent
import org.koin.core.inject

/**
 * Presenter for product list [com.bux.activity.MainActivity]
 */
class MainPresenter(private val view: MainContract.View) : MainContract.Presenter, KoinComponent {

    private val repo : ProductRepository by inject()

    private val disposables = CompositeDisposable()

    override fun start(bundle: Bundle?) {
        disposables.add(repo.getAll().observeOn(AndroidSchedulers.mainThread()).subscribe { items ->
            view.showList(items)
        })
    }

    override fun click(item: Product) {
        view.showDetail(item)
    }

    override fun stop() {
        disposables.clear()
    }
}