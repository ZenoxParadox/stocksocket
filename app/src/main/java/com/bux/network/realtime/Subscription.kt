package com.bux.network.realtime

/**
 * TODO Describe class functionality.
 */
data class Subscription(
    private val subscribeTo: MutableSet<String> = mutableSetOf(),
    private val unsubscribeFrom: MutableSet<String> = mutableSetOf()
) {

    fun subscribe(productId: String) {
        subscribeTo.add("trading.product.${productId}")
        unsubscribeFrom.remove("trading.product.${productId}")
    }

    fun unsubscribe(productId: String) {
        unsubscribeFrom.add("trading.product.${productId}")
        subscribeTo.remove("trading.product.${productId}")
    }

}