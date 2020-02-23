package com.bux.presenter.contract.base

interface BaseView<T> {
    fun setPresenter(presenter: T)
}