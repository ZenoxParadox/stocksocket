package com.bux.network.repository

import com.bux.domain.dao.ProductDao
import com.bux.domain.model.Product
import com.bux.network.rest.RestApi
import com.bux.util.Logger
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import java.net.ConnectException
import javax.inject.Inject

/**
 * Basic [Product] repository between [RestApi] and [com.bux.domain.BuxDatabase]
 */
class ProductRepository @Inject constructor(private val api: RestApi, private val productDao: ProductDao) {

    private val LOG_TAG = this::class.java.simpleName

    init {
        Logger.addIgnoreTag(LOG_TAG)
    }

    fun getAll(): Single<List<Product>> {
        return productDao.getAll().subscribeOn(Schedulers.io())
    }

    fun getSingle(securityId: String): Single<Product> {
        return api.getProduct(securityId)
            .onErrorResumeNext error@{ error ->
                Logger.e(LOG_TAG, "onErrorResumeNext(${error.message})")

                if (error is ConnectException) {
                    Logger.v(LOG_TAG, "rescued with local copy.")
                    return@error productDao.getProduct(securityId)
                }

                throw error
            }
            .subscribeOn(Schedulers.io())
            .doAfterSuccess { item ->
                productDao.update(item).subscribe { count ->
                    Logger.v(LOG_TAG, "update count: $count")
                }
            }
    }

}