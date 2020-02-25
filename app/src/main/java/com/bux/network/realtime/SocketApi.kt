package com.bux.network.realtime

import com.tinder.scarlet.WebSocket
import com.tinder.scarlet.ws.Receive
import com.tinder.scarlet.ws.Send
import io.reactivex.Flowable

/**
 * TODO Describe class functionality.
 */
interface SocketApi {

    @Receive
    fun eventStream(): Flowable<WebSocket.Event>

    @Send
    fun sendSubscription(subscribe: Subscription)

    @Receive
    fun mainStream(): Flowable<BuxMessage>

}