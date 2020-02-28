package com.bux

import android.app.Application
import android.os.Looper
import com.bux.di.AppComponent
import com.bux.di.AppModule
import com.bux.di.DaggerAppComponent
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.android.schedulers.AndroidSchedulers

class Bux : Application() {

    companion object {
        lateinit var dagger: AppComponent
    }

    override fun onCreate() {
        super.onCreate()

        // dagger
        dagger = DaggerAppComponent.builder()
            .appModule(AppModule(this))
            .build()

        // Make use of async scheduler
        val scheduler = AndroidSchedulers.from(Looper.getMainLooper(), true)
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { scheduler }
    }
}