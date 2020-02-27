package com.bux.network.realtime

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName

/**
 * Generic BuxMessage.  Can be serialized based on the type (serialized: t) variable into
 * either [Quote] or [Connected]
 */
data class BuxMessage(

    @SerializedName("t")
    val type: MessageType = MessageType.NONE,

    val body: JsonElement

) {

    fun getQuote(): Quote? {
        if (type == MessageType.QUOTE) {
            return Gson().fromJson(body, Quote::class.java)
        }

        return null
    }

    fun getConnected(): Connected? {
        if (type == MessageType.CONNECTED) {
            return Gson().fromJson(body, Connected::class.java)
        }

        return null
    }

}

data class Connected(
    val userId: String,
    val sessionId: String,
    val time: Long
)

data class Quote(
    val securityId: String,
    val currentPrice: Double,
    val timeStamp: Long
)
