package com.bux.network.realtime

/**
 * TODO Describe class functionality.
 * TODO try to make a BaseMessgae<T> class
 *
 * Created by Zenox on 21-2-2020 at 11:34.
 */
data class QuoteMessage(
    val securityId: String,
    val currentPrice: Double,
    val timeStamp: Long
)

