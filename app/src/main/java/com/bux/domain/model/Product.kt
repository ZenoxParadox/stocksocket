package com.bux.domain.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

const val PERCENTAGE_FORMAT = "%.2f%%"

/**
{
"symbol": "FRANCE40",
"securityId": "26608",
"displayName": "French Exchange",
"currentPrice": {
"currency": "EUR",
"decimals": 1,
"amount": "4371.8"
},
"closingPrice": {
"currency": "EUR",
"decimals": 1,
"amount": "4216.4"
}
}

 *
 * Created by Zenox on 20-2-2020 at 12:32.
 */
@Entity
data class Product(

    @PrimaryKey
    val securityId: String,

    @ColumnInfo
    val symbol: String,

    @ColumnInfo
    val displayName: String,

    @Embedded(prefix = "current_")
    var currentPrice: Price?, // TODO enable + make updatable

    @Embedded(prefix = "closing_")
    var closingPrice: Price?
) {


    /**
     * Can be negative growth!
     */
    fun getGrowth(): Double {
        currentPrice?.let { new ->
            closingPrice?.let { old ->
                val difference = new.amount - old.amount
                return (difference / old.amount) * 100
            }
        }

        return 0.0
    }


}
