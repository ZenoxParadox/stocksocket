package com.bux

import androidx.room.Room
import com.bux.domain.BuxDatabase
import com.bux.domain.dao.ProductDao
import com.bux.network.Api
import com.bux.network.Network
import com.bux.network.realtime.Socket
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

const val NAME = "Bux"

/**
 * TODO Describe class functionality.
 *
 * Created by Zenox on 20-2-2020 at 12:19.
 */
val modules = module {
    single<Api> { Network().create() }
    single<Socket> { Socket() }

    single<BuxDatabase> {
        val builder = Room.databaseBuilder(androidContext(), BuxDatabase::class.java, NAME)
        builder.fallbackToDestructiveMigration()

        builder.addCallback(BuxDatabase.Initialization)
        builder.build()
    }
    single<ProductDao> {
        val database: BuxDatabase = get()
        database.productDao()
    }

    single<Gson>{
        GsonBuilder()
        //.excludeFieldsWithoutExposeAnnotation()
        //.registerTypeAdapter(Calendar::class.java, CalenderConverter())
        .create()
    }

}