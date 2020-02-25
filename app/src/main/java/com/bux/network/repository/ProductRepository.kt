package com.bux.network.repository

import com.bux.domain.dao.ProductDao
import com.bux.domain.model.Product
import com.bux.network.rest.RestApi
import com.bux.util.Logger
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.net.ConnectException

/**
 * TODO Describe class functionality.
 */
class ProductRepository : KoinComponent {

    private val LOG_TAG = this::class.java.simpleName

    private val api: RestApi by inject()

    private val productDao: ProductDao by inject()

    fun getAll(): Single<List<Product>> {
        return productDao.getAll().subscribeOn(Schedulers.io())
    }

    // TODO Database -> Flowable<Product>

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