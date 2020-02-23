package com.bux.network

import com.bux.BuildConfig
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

/**
 * TODO Describe class functionality.
 *
 * Created by Zenox on 20-2-2020 at 12:13.
 */
class Network {

    fun create(): Api {
        val gson = GsonBuilder().create()

        val interceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val client = OkHttpClient.Builder().addInterceptor(interceptor).build()

        //
        val retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(client)
            .build()

        return retrofit.create<Api>(Api::class.java)
    }

}