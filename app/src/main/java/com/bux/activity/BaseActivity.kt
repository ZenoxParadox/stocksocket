package com.bux.activity

import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatActivity
import com.bux.network.NetworkConnectionReceiver
import com.bux.util.Logger
import io.reactivex.exceptions.OnErrorNotImplementedException
import io.reactivex.plugins.RxJavaPlugins
import java.net.ConnectException


/**
 * TODO Describe class functionality.
 *
 * Created by Zenox on 21-2-2020 at 10:13.
 */
abstract class BaseActivity : AppCompatActivity() {

    private val LOG_TAG = BaseActivity::class.java.simpleName

    private lateinit var connectionReceiver: NetworkConnectionReceiver

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /*
         * Catching undeliverable exceptions. Not all should be considered runtime exceptions.
         */
        RxJavaPlugins.setErrorHandler handler@{ error ->
            if (error is OnErrorNotImplementedException) {
                when (error.cause) {
                    is ConnectException -> {
                        // TODO show snackbar or something
                        Toast.makeText(this@BaseActivity, error.cause?.message, Toast.LENGTH_SHORT)
                            .show()

                        return@handler
                    }
                }
            }

            // If the exception still hasn't been handled, rethrow it.
            Logger.e(LOG_TAG, "Undeliverable exception received. No action specified", error)
            throw RuntimeException(error)
        }
    }

    internal fun setConnectionAware() {
        Logger.i(LOG_TAG, "setConnectionAware()")

        connectionReceiver = NetworkConnectionReceiver(this, object : NetworkConnectionReceiver.Callback {
            override fun connectionWorking() {
                onConnection()
            }
        })

        val filter = IntentFilter()
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(connectionReceiver, filter)
    }

    open fun onConnection() {
        throw NotImplementedError("Called yet not implemented")
    }

    override fun onDestroy() {
        super.onDestroy()

        if (this::connectionReceiver.isInitialized) {
            unregisterReceiver(connectionReceiver)
        }
    }

}