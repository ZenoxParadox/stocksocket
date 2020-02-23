package com.bux.domain.dao

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update
import io.reactivex.Single

/**
 * TODO Describe class functionality.
 *
 */
interface BaseDao<T> {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAll(items: List<T>): Single<List<Long>>

    @Insert
    fun insert(item: T): Single<Long>

    @Delete
    fun delete(item: T): Single<Int>

    @Update
    fun update(item: T): Single<Int>

}