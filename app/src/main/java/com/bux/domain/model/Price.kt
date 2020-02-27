package com.bux.domain.model

import java.text.NumberFormat
import java.util.*

/**
 * Price model for a [Product]
 */
data class Price(

    val currency: String,

    val decimals: Int,

    var amount: Double
){
    fun asCurrency(number: Double = amount): String {
        val formatter: NumberFormat = NumberFormat.getCurrencyInstance()
        formatter.currency = Currency.getInstance(currency)

        formatter.minimumFractionDigits = decimals
        formatter.maximumFractionDigits = decimals

        return formatter.format(number)
    }

}


