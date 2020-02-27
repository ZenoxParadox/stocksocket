package com.bux.domain

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.bux.domain.dao.ProductDao
import com.bux.domain.model.Product

/**
 * Database configuration file
 */
@Database(entities = [Product::class], version = 1)
abstract class BuxDatabase : RoomDatabase(){

    abstract fun productDao(): ProductDao

    /**
     * Fill the database with the predefined products.
     */
    object Initialization : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

            db.beginTransaction()
            db.execSQL("INSERT INTO Product (symbol, securityId, displayName) VALUES ('GERMANY30', 'sb26493', 'Germany 30')")
            db.execSQL("INSERT INTO Product (symbol, securityId, displayName) VALUES ('EUR/USD', 'sb26502', 'EUR/USD')")
            db.execSQL("INSERT INTO Product (symbol, securityId, displayName) VALUES ('US500', 'sb26496', 'US 500')")
            db.execSQL("INSERT INTO Product (symbol, securityId, displayName) VALUES ('Gold', 'sb26500', 'Gold')")
            db.execSQL("INSERT INTO Product (symbol, securityId, displayName) VALUES ('AAPL', 'sb26513', 'Apple')")
            db.execSQL("INSERT INTO Product (symbol, securityId, displayName) VALUES ('Deutsche Bank', 'sb28248', 'Deutsche Bank')")
            db.setTransactionSuccessful()
            db.endTransaction()
        }
    }

}