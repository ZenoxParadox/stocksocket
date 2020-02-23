package com.bux.network.realtime

/**
 * TODO Describe class functionality.
 * TODO try to make a BaseMessgae<T> class
 *
 * Created by Zenox on 21-2-2020 at 11:34.
 */
data class ConnectionMessage(
    val userId: String,
    val sessionId: String,
    val time: Long
)