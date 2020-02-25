package com.bux.activity

import android.os.Bundle
import android.widget.Toast
import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatActivity
import com.bux.util.Logger
import io.reactivex.exceptions.OnErrorNotImplementedException
import io.reactivex.plugins.RxJavaPlugins
import java.net.ConnectException

/**
 * TODO Describe class functionality.
 */
abstract class BaseActivity : AppCompatActivity() {

    private val LOG_TAG = BaseActivity::class.java.simpleName

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

}