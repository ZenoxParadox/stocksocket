package com.bux.network.realtime

import com.google.gson.Gson

/**
 * TODO Describe class functionality.
 *
 * Created by Zenox on 21-2-2020 at 10:41.
 */
data class SocketEvent(
    private val subscribeTo: MutableSet<String> = mutableSetOf(),
    private val unsubscribeFrom: MutableSet<String> = mutableSetOf()
) {

    fun subscribe(productId: String) {
        subscribeTo.add("trading.product.${productId}")
        unsubscribeFrom.remove(productId)
    }

    fun unsubscribe(productId: String) {
        unsubscribeFrom.add("trading.product.${productId}")
        subscribeTo.remove(productId)
    }

    fun toJson(gson: Gson): String {
        return gson.toJson(this)
    }

}