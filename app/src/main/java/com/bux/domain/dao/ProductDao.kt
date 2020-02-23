package com.bux.domain.dao

import androidx.room.Dao
import androidx.room.Query
import com.bux.domain.model.Product
import io.reactivex.Single

@Dao
interface ProductDao : BaseDao<Product> {

    @Query("SELECT * FROM Product")
    fun getAll(): Single<List<Product>>

    @Query("SELECT * FROM Product WHERE securityId = :id LIMIT 1")
    fun getProduct(id: String): Single<Product>

    @Query("UPDATE Product SET current_amount = :current WHERE securityId = :securityId")
    fun setCurrent(securityId: String, current: Double): Int

}