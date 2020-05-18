package com.bux.activity

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.CallSuper
import androidx.annotation.IntRange
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bux.R
import com.bux.network.rest.BuxException
import com.bux.network.rest.ErrorType
import com.bux.network.rest.findOrNull
import com.bux.util.Logger
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import io.reactivex.exceptions.CompositeException
import io.reactivex.exceptions.OnErrorNotImplementedException
import io.reactivex.plugins.RxJavaPlugins
import java.net.ConnectException
import java.net.SocketTimeoutException

/**
 * Base activity
 */
abstract class BaseActivity : AppCompatActivity() {

    private val LOG_TAG = this::class.java.simpleName

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /**
         * Catching undeliverable exceptions. Not all should be considered runtime exceptions.
         *
         * NOTE: this could be the main place to handle errors that can come from any place. Also
         * note that these are only the errors that are not handled already (OnErrorNotImplementedException).
         *
         * When not handled they can be considered crashes/bugs (firebase bug report for example)
         */
        RxJavaPlugins.setErrorHandler handler@{ error ->
            Logger.block(LOG_TAG, "MAIN ERROR")

            if (error is OnErrorNotImplementedException) {
                Logger.w(LOG_TAG, "cause: ${error.cause?.message}")

                when (error.cause) {
                    is ConnectException -> {
                        Toast.makeText(this@BaseActivity, error.cause?.message, Toast.LENGTH_SHORT)
                            .show()

                        return@handler
                    }
                }
            }

            if (error is CompositeException) {
                findOrNull<BuxException>(error)?.let { buxError ->
                    Logger.w(LOG_TAG, "BuxError: ${buxError.hint}. (${buxError.code})")

                    when (buxError.code) {
                        ErrorType.ACCESS_PERMISSION, ErrorType.EXPIRED, ErrorType.AUTH_HEADER -> {
                            finish()
                            return@handler
                        }
                        ErrorType.INVALIDTOKEN -> {
                            showIssue(buxError.code.text, Toast.LENGTH_SHORT)
                            finish()
                            return@handler
                        }
                        ErrorType.UNEXPECTED -> {
                            showIssue(buxError.code.text, Toast.LENGTH_SHORT)
                        }
                    }
                }

                findOrNull<SocketTimeoutException>(error)?.let { socketError ->
                    Logger.w(LOG_TAG, "Socket error: ${socketError.message}")

                    var message = "Connection error"
                    socketError.message?.let {
                        message = it
                    }

                    showError(message, Snackbar.LENGTH_SHORT)
                    return@handler
                }

            }

            // If the exception still hasn't been handled, rethrow it.
            Logger.block(LOG_TAG, "Undeliverable exception received. No action specified")
            throw error
        }
    }

    /**
     * Used in case the user could ignore the message. Whatever the message says, it should not
     * require direct user input.
     *
     * Note that [duration] should come from either 0 or 1 (found in [Toast])
     */
    private fun showIssue(text: Int, @IntRange(from = 0, to = 1) duration: Int) {
        val layout: View = layoutInflater.inflate(R.layout.toast, null)
        val tvText = layout.findViewById<TextView>(R.id.toast_text)
        tvText.text = getString(text)

        with(Toast(applicationContext)) {
            setGravity(Gravity.BOTTOM, 0, 0)
            view = layout
            this.duration = duration
            show()
        }
    }

    /**
     * Used in case the user needs to acknowledge the message
     */
    private fun showError(text: String, @BaseTransientBottomBar.Duration duration: Int) {
        val bar = Snackbar.make(window.decorView, text, duration)
        bar.setAction(android.R.string.ok) {
            bar.dismiss()
        }
        bar.view.setBackgroundColor(ContextCompat.getColor(bar.view.context, R.color.king_red))
        bar.show()
    }

    /**
     * Used in case the user needs to acknowledge the message
     */
    private fun showError(text: Int, @BaseTransientBottomBar.Duration duration: Int) {
        val stringText = getString(text)
        showError(stringText, duration)
    }

}