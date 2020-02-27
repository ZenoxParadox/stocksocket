package com.bux.domain.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

const val PERCENTAGE_FORMAT = "%.2f%%"

/**
 * Product model
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
    var currentPrice: Price?,

    @Embedded(prefix = "closing_")
    var closingPrice: Price?
) {

    /**
     * Gives the growth between [currentPrice] and [closingPrice] in percentage. Can also result
     * in negative growth.
     *
     * When either is unknown the growth is 0.0.
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