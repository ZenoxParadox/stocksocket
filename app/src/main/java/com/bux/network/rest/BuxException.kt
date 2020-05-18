package com.bux.network.rest

import com.google.gson.annotations.SerializedName
import io.reactivex.exceptions.CompositeException

/**
 * Errors that can be returned from the Bux api
 */
class BuxException(
    override val message: String?,

    @SerializedName("developerMessage")
    val hint: String,

    @SerializedName("errorCode")
    val code: ErrorType
) : Exception(message)

/**
 * Finds a specific type of error from the [collection] and returns that. If it cannot be found
 * the whole collection will be rethrown.
 */
inline fun <reified T : Throwable> findOrThrow(collection: CompositeException): T {
    for (item in collection.exceptions) {
        if (item::class.java.simpleName == T::class.java.simpleName) {
            return item as T
        }
    }
    throw collection
}

/**
 * Finds a specific type of error from the [collection] and returns that. If it cannot be found
 * the whole collection will be rethrown.
 */
inline fun <reified T : Throwable> findOrNull(collection: CompositeException): T? {
    for (item in collection.exceptions) {
        if (item::class.java.simpleName == T::class.java.simpleName) {
            return item as T
        }
    }
    return null
}

