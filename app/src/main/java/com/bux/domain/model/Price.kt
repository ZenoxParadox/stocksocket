package com.bux.domain.model

import java.text.NumberFormat
import java.util.*

/**
 * TODO: check Currency codes are in the ISO 4217 format
 *
 * Created by Zenox on 20-2-2020 at 12:32.
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


