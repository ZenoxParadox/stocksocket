package com.bux.domain.model

import org.junit.Assert
import org.junit.Test

/**
 * Tests for [com.bux.domain.model.Product]
 */
class ProductTest {

    @Test
    fun `a1 - should have double growth`() {
        val closingPrice = Price(currency = "EUR", amount = 1.0, decimals = 5)
        val currentPrice = Price(currency = "EUR", amount = 2.0, decimals = 5)
        val product = Product(
            securityId = "abc",
            symbol = "s",
            displayName = "example",
            closingPrice = closingPrice,
            currentPrice = currentPrice
        )

        Assert.assertEquals(100.0, product.getGrowth(), 0.0)
    }

    @Test
    fun `a2 - should have zero growth`() {
        val closingPrice = Price(currency = "EUR", amount = 1.0, decimals = 5)
        val currentPrice = Price(currency = "EUR", amount = 1.0, decimals = 5)
        val product = Product(
            securityId = "abc",
            symbol = "s",
            displayName = "example",
            closingPrice = closingPrice,
            currentPrice = currentPrice
        )

        Assert.assertEquals(0.0, product.getGrowth(), 0.0)
    }

    @Test
    fun `a3 - should have growth halved`() {
        val closingPrice = Price(currency = "EUR", amount = 1.0, decimals = 5)
        val currentPrice = Price(currency = "EUR", amount = 0.5, decimals = 5)
        val product = Product(
            securityId = "abc",
            symbol = "s",
            displayName = "example",
            closingPrice = closingPrice,
            currentPrice = currentPrice
        )

        Assert.assertEquals(-50.0, product.getGrowth(), 0.0)
    }

    @Test
    fun `a4 - growth should be zero when closing price is missing`() {
        val currentPrice = Price(currency = "EUR", amount = 0.5, decimals = 5)
        val product = Product(
            securityId = "abc",
            symbol = "s",
            displayName = "example",
            closingPrice = null,
            currentPrice = currentPrice
        )

        Assert.assertEquals(0.0, product.getGrowth(), 0.0)
    }

    @Test
    fun `a5 - growth should be zero when current price is missing`() {
        val closingPrice = Price(currency = "EUR", amount = 1.0, decimals = 5)
        val product = Product(
            securityId = "abc",
            symbol = "s",
            displayName = "example",
            closingPrice = closingPrice,
            currentPrice = null
        )

        Assert.assertEquals(0.0, product.getGrowth(), 0.0)
    }


}