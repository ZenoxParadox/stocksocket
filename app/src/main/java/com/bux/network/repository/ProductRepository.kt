package com.bux.network.repository

import com.bux.domain.dao.ProductDao
import com.bux.domain.model.Product
import com.bux.network.Api
import com.bux.util.Logger
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.net.ConnectException

/**
 * TODO Describe class functionality.
 *
 * Created by Zenox on 20-2-2020 at 15:58.
 */
class ProductRepository : KoinComponent {

    private val LOG_TAG = this::class.java.simpleName

    private val api: Api by inject()

    private val productDao: ProductDao by inject()

    // private val database: DataSource<Database> by inject() TODO <--------

    // private val api: DataSource<Api> by inject() TODO <--------
    // private val socket: DataSource<Socket> by inject() TODO <--------

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

//    fun getSingleFlow(id: String): Flowable<Product> {
////        val event = SocketEvent()
////        event.subscribe(id)
////
////        val data = event.toJson(gson)
////        Logger.toString(LOG_TAG, "data", data)
////
////
////        val connection = socket.openConnection(BaseSocketListener(gson, object: BaseSocketListener.Listener {
////
////            override fun onMessage(message: QuoteMessage) {
////
////                emitter.onNext(message)
////
////            }
////
////        }))
////
////        connection.send(data)
//
//        return productDao.getProduct(id).observeOn(Schedulers.io())
//    }

}