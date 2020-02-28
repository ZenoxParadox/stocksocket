package com.bux.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.bux.domain.BuxDatabase
import com.bux.domain.NAME
import com.bux.domain.dao.ProductDao
import com.bux.network.realtime.Socket
import com.bux.network.realtime.SocketApi
import com.bux.network.repository.ProductRepository
import com.bux.network.rest.Network
import com.bux.network.rest.RestApi
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import javax.inject.Named
import javax.inject.Singleton

/**
 * TODO Describe class functionality.
 *
 * Created by Zenox on 27-2-2020 at 13:17.
 */
@Module
class AppModule(private val app: Application) {

    @Provides
    @Singleton
    @Named("appContext")
    fun provideContext(): Context = app.applicationContext

    @Provides
    @Singleton
    fun provideRestApi(gson: Gson): RestApi {
        return Network(gson).create()
    }

    @Provides
    @Singleton
    fun provideSocketApi(): SocketApi {
        return Socket().create()
    }

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder().create()
    }

    @Provides
    @Singleton
    fun provideDatabase(@Named("appContext") context: Context): BuxDatabase {
        val builder = Room.databaseBuilder(context, BuxDatabase::class.java, NAME)
        builder.fallbackToDestructiveMigration()

        builder.addCallback(BuxDatabase.Initialization)
        return builder.build()
    }

    @Provides
    @Singleton
    fun provideProductDao(db: BuxDatabase): ProductDao {
        return db.productDao()
    }

    /* ********** [ objects ] ********** */

    @Provides
    @Singleton
    fun provideProductRepository(api: RestApi, productDao: ProductDao): ProductRepository {
        return ProductRepository(api, productDao)
    }

}