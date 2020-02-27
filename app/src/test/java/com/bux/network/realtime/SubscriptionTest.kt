package com.bux.network.realtime

import org.junit.Assert
import org.junit.Test

/**
 * Tests for [com.bux.network.realtime.Subscription]
 */
class SubscriptionTest {

    @Test
    fun `a1 - should have no subscriptions`() {
        val subscription = Subscription()

        Assert.assertEquals(
            "Subscription(subscribeTo=[], unsubscribeFrom=[])",
            subscription.toString()
        )
    }

    @Test
    fun `a2 - should add subscriptions`() {
        val subscription = Subscription()
        subscription.subscribe("123")

        Assert.assertEquals(
            "Subscription(subscribeTo=[trading.product.123], unsubscribeFrom=[])",
            subscription.toString()
        )
    }

    @Test
    fun `a3 - should have many subscriptions`() {
        val subscription = Subscription()
        subscription.subscribe("123")
        subscription.subscribe("456")
        subscription.subscribe("789")

        Assert.assertEquals(
            "Subscription(subscribeTo=[trading.product.123, trading.product.456, trading.product.789], unsubscribeFrom=[])",
            subscription.toString()
        )
    }

    @Test
    fun `a4 - should remove subscriptions`() {
        val subscription = Subscription()
        subscription.subscribe("123")
        subscription.subscribe("456") // <-- notice
        subscription.subscribe("789")

        subscription.unsubscribe("456") // <-- notice

        Assert.assertEquals(
            "Subscription(subscribeTo=[trading.product.123, trading.product.789], unsubscribeFrom=[trading.product.456])",
            subscription.toString()
        )
    }

    @Test
    fun `a5 - should only remove subscription once when calling unsubscribe twice`() {
        val subscription = Subscription()
        subscription.subscribe("123")
        subscription.subscribe("456") // <-- notice
        subscription.subscribe("789")

        subscription.unsubscribe("456") // <-- notice
        subscription.unsubscribe("456") // <-- notice

        Assert.assertEquals(
            "Subscription(subscribeTo=[trading.product.123, trading.product.789], unsubscribeFrom=[trading.product.456])",
            subscription.toString()
        )
    }


}