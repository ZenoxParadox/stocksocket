package com.bux

import android.app.Application
import android.os.Looper
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.android.schedulers.AndroidSchedulers
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class Bux : Application() {

    override fun onCreate() {
        super.onCreate()

        // Koin di
        startKoin {
            //androidLogger(Level.DEBUG)
            androidContext(this@Bux)
            modules(modules)
        }

        // Make use of async scheduler
        val scheduler = AndroidSchedulers.from(Looper.getMainLooper(), true)
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { scheduler }
    }
}