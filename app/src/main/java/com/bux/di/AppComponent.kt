package com.bux.di

import com.bux.presenter.DetailPresenter
import com.bux.presenter.MainPresenter
import dagger.Component
import javax.inject.Singleton

/**
 * TODO Describe class functionality.
 */
@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {

    fun inject(target: MainPresenter)

    fun inject(target: DetailPresenter)

}