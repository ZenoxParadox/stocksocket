package com.bux.network.realtime

import com.tinder.scarlet.WebSocket
import com.tinder.scarlet.ws.Receive
import com.tinder.scarlet.ws.Send
import io.reactivex.Flowable

/**
 * Socket definition for subscribing to [com.bux.domain.model.Product] updates.
 *
 * 1) Open stream (+ authenticate)
 * 2) Wait for [Connected] message
 * 3) Subscribe using [sendSubscription]
 * 4) Wait for updates coming from [mainStream]
 */
interface SocketApi {

    @Receive
    fun eventStream(): Flowable<WebSocket.Event>

    @Send
    fun sendSubscription(subscribe: Subscription)

    @Receive
    fun mainStream(): Flowable<BuxMessage>

}