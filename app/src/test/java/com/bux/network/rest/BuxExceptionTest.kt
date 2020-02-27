package com.bux.network.rest

import com.google.gson.Gson
import org.junit.Assert
import org.junit.Test

/**
 * Bux error test
 */
class BuxExceptionTest {

    private val errorBody = "{\"message\":\"No user found for token: abcdef\",\"developerMessage\":\"No user found for token: abcdef\",\"errorCode\":\"AUTH_007\"}"

    private val gson = Gson()

    @Test
    fun `a1 - should serialize the bux error correctly`(){
        val error = gson.fromJson(errorBody, BuxException::class.java)
        Assert.assertEquals("No user found for token: abcdef", error.hint)
    }

}