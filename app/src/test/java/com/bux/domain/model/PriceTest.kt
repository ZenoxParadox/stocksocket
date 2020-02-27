package com.bux.domain.model

import org.junit.Assert
import org.junit.Test
import java.util.*

/**
 * Tests for [com.bux.domain.model.Price]
 */
class PriceTest {

    /* ********** [ locale ] ********** */

    @Test
    fun `a1 - should format EURO in US locale`() {
        val price = Price(currency = "EUR", amount = 1_000.000, decimals = 5)

        Locale.setDefault(Locale.US)

        Assert.assertEquals("EUR1,000.00000", price.asCurrency())
    }

    @Test
    fun `a2 - should format EURO in UK format`() {
        val price = Price(currency = "EUR", amount = 1_000.000, decimals = 5)

        Locale.setDefault(Locale.UK)

        Assert.assertEquals("€1,000.00000", price.asCurrency())
    }

    @Test
    fun `a3 - should format in EURO German format`() {
        val price = Price(currency = "EUR", amount = 1_000.000, decimals = 5)

        Locale.setDefault(Locale.GERMAN)

        Assert.assertEquals("EUR 1.000,00000", price.asCurrency())
    }

    @Test
    fun `a4 - should format EURO in NL format`() {
        val price = Price(currency = "EUR", amount = 1_000.000, decimals = 5)

        Locale.setDefault(Locale("nl", "NL"))

        Assert.assertEquals("€ 1.000,00000", price.asCurrency())
    }

    @Test
    fun `a5 - should format USD in US format`() {
        val price = Price(currency = "USD", amount = 1_000.000, decimals = 2)

        Locale.setDefault(Locale.US)

        Assert.assertEquals("$1,000.00", price.asCurrency())
    }

    @Test
    fun `a6 - should format USD in NL locale`() {
        val price = Price(currency = "USD", amount = 1_000.000, decimals = 2)

        Locale.setDefault(Locale("nl", "NL"))

        Assert.assertEquals("USD 1.000,00", price.asCurrency())
    }

    /* ********** [ parsing decimals ] ********** */

    @Test
    fun `b1 - should not show decimals`() {
        Locale.setDefault(Locale("nl", "NL"))

        val price = Price(currency = "EUR", amount = 1_000.000, decimals = 0)

        Assert.assertEquals("€ 1.000", price.asCurrency())
    }

    @Test
    fun `b2 - should give zero decimals when decimal count is negative`() {
        Locale.setDefault(Locale("nl", "NL"))

        val price = Price(currency = "EUR", amount = 1_000.000, decimals = -1)

        Assert.assertEquals("€ 1.000", price.asCurrency())
    }

    @Test
    fun `b3 - should have negative indicator in NL locale`() {
        Locale.setDefault(Locale("nl", "NL"))

        val price = Price(currency = "EUR", amount = -1_000.000, decimals = 0)

        Assert.assertEquals("€ 1.000-", price.asCurrency())
    }

    @Test
    fun `b4 - should have negative indicator in US locale`() {
        Locale.setDefault(Locale.US)

        val price = Price(currency = "EUR", amount = -1_000.000, decimals = 0)

        Assert.assertEquals("(EUR1,000)", price.asCurrency())
    }

    /* ********** [ currency ] ********** */

    @Test
    fun `c1 - should group numbers in NL locale`() {
        Locale.setDefault(Locale("nl", "NL"))

        val price = Price(currency = "USD", amount = 1_000_000.000_000, decimals = 8)

        Assert.assertEquals("USD 1.000.000,00000000", price.asCurrency())
    }

    @Test
    fun `c2 - should group numbers in US locale`() {
        Locale.setDefault(Locale.US)

        val price = Price(currency = "USD", amount = 1_000_000.000_000, decimals = 8)

        Assert.assertEquals("$1,000,000.00000000", price.asCurrency())
    }

}