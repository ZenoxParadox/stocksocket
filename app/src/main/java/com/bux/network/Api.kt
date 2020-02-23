package com.bux.network

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
const val TOKEN_REST = "eyJhbGciOiJIUzI1NiJ9.eyJyZWZyZXNoYWJsZSI6ZmFsc2UsInN1YiI6ImJiMGNkYTJiLWExMGUtNGVkMy1hZDVhLTBmODJiNGMxNTJjNCIsImF1ZCI6ImJldGEuZ2V0YnV4LmNvbSIsInNjcCI6WyJhcHA6bG9naW4iLCJydGY6bG9naW4iXSwiZXhwIjoxODIwODQ5Mjc5LCJpYXQiOjE1MDU0ODkyNzksImp0aSI6ImI3MzlmYjgwLTM1NzUtNGIwMS04NzUxLTMzZDFhNGRjOGY5MiIsImNpZCI6Ijg0NzM2MjI5MzkifQ.M5oANIi2nBtSfIfhyUMqJnex-JYg6Sm92KPYaUL9GKg"

/**
 * TODO Describe class functionality.
 *
 * Created by Zenox on 20-2-2020 at 12:13.
 */
interface Api {

    @Headers(
        "Authorization: Bearer $TOKEN_REST",
        "Accept: application/json",
        "Accept-Language: nl-NL,en;q=0.8"
    )
    @GET("/core/21/products/{productId}")
    fun getProduct(@Path("productId") productId: String): Single<Product>

}