package com.bux.network.rest

import com.bux.BuildConfig
import com.bux.domain.model.Product
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path

/*

TODO remove
{
  "refreshable": false,
  "sub": "bb0cda2b-a10e-4ed3-ad5a-0f82b4c152c4",
  "aud": "beta.getbux.com",
  "scp": [
    "app:login",
    "rtf:login"
  ],
  "exp": 1820849279,
  "iat": 1505489279,
  "jti": "b739fb80-3575-4b01-8751-33d1a4dc8f92",
  "cid": "8473622939"
}
 */

/**
 * TODO Describe class functionality.
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