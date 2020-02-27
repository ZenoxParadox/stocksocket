package com.bux.network.rest

import com.bux.BuildConfig
import com.bux.domain.model.Product
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path

/**
 * Rest api endpoints
 */
interface RestApi {

    @Headers(
        "Authorization: Bearer ${BuildConfig.TOKEN_REST}",
        "Accept: application/json",
        "Accept-Language: nl-NL,en;q=0.8"
    )
    @GET("/core/21/products/{productId}")
    fun getProduct(@Path("productId") productId: String): Single<Product>

}