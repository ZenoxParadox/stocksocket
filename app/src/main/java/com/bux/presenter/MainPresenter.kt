package com.bux.presenter

import com.bux.domain.model.Product
import com.bux.presenter.contract.MainContract
import com.bux.network.repository.ProductRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable

/**
 * TODO Describe class functionality.
 *
 * Created by Zenox on 21-2-2020 at 20:55.
 */
class MainPresenter(private val view: MainContract.View) : MainContract.Presenter {

    val repo = ProductRepository()

    private val disposables = CompositeDisposable()

    override fun start() {
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