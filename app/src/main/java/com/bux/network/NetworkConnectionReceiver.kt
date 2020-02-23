package com.bux.network

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import com.bux.util.Logger

/**
 * TODO Describe class functionality.
 */
class NetworkConnectionReceiver(context: Context, private val listener: Callback) :
    BroadcastReceiver() {

    private val LOG_TAG = this::class.java.simpleName

    private var isConnected = isConnectedNow(context)

    private fun isConnectedNow(context: Context): Boolean {
        val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?

        if (manager != null && manager.activeNetworkInfo != null) {
            return manager.activeNetworkInfo.isConnected
        }

        return false
    }

    override fun onReceive(context: Context, intent: Intent) {
        Logger.i(LOG_TAG, "onReceive($intent})")

        if (intent.action == ConnectivityManager.CONNECTIVITY_ACTION) {
            val previouslyConnected = isConnected

            Logger.d(LOG_TAG, "previouslyConnected: $previouslyConnected")
            Logger.d(LOG_TAG, "isConnectedNow: ${isConnectedNow(context)}")

            if (!previouslyConnected && isConnectedNow(context)) {
                Logger.v(LOG_TAG, "checking that now")
                listener.connectionWorking()
            }

            isConnected = isConnectedNow(context)
        }
    }

    interface Callback {

        fun connectionWorking()

    }

}