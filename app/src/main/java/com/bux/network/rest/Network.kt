package com.bux.network.rest

import com.bux.BuildConfig
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject

/**
 * Network for REST calls.
 *
 * Custom error body is caught here and thrown as a new exception.
 */
class Network @Inject constructor(private val gson: Gson) {

    private val LOG_TAG = this::class.java.simpleName

    fun create(): RestApi {

        val interceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.NONE
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .addInterceptor intercept@{ chain ->

                val response = chain.proceed(chain.request())
                if (!response.isSuccessful) {
                    val body: String? = response.body?.string()

                    body?.let {
                        val buxError = gson.fromJson(body, BuxException::class.java)
                        throw buxError
                    }
                }

                return@intercept response
            }
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(client)
            .build()

        return retrofit.create<RestApi>(RestApi::class.java)
    }

}