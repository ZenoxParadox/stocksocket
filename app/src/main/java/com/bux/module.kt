package com.bux

import androidx.room.Room
import com.bux.domain.BuxDatabase
import com.bux.network.realtime.Socket
import com.bux.network.rest.Network
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

const val NAME = "Bux"

/**
 * TODO Describe class functionality.
 */
val modules = module {
    single { Network().create() }
    factory { Socket().create() }

    single {
        val builder = Room.databaseBuilder(androidContext(), BuxDatabase::class.java, NAME)
        builder.fallbackToDestructiveMigration()

        builder.addCallback(BuxDatabase.Initialization)
        builder.build()
    }

    single {
        val database: BuxDatabase = get()
        database.productDao()
    }

    single<Gson> { GsonBuilder().create() }

}