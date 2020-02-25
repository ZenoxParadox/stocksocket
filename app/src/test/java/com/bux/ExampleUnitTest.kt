package com.bux

import com.bux.network.realtime.BuxMessage
import com.bux.network.realtime.Connected
import com.bux.network.realtime.MessageType
import com.bux.network.realtime.Quote
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import org.junit.Assert
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {

    //inline fun <reified T> Gson.fromJson(json: String) = fromJson<T>(json, object: TypeToken<T>() {}.type)

    private val gson: Gson = GsonBuilder()
        //.registerTypeAdapterFactory(factory)
        .create()

    private val BODY_CONNECTED = "{ \"body\": { \"userId\": \"bb0cda2b-a10e-4ed3-ad5a-0f82b4c152c4\", \"sessionId\":\"b1c30e28-a2b3-4b1b-8bcb-f9be85a50c17\", \"time\": 1582285348913 }, \"t\": \"connect.connected\" }"

    private val BODY_QUOTE = "{ \"body\": { \"securityId\": \"sb26502\", \"currentPrice\": \"1.29079\", \"timeStamp\": 1582231088710 }, \"t\": \"trading.quote\" }"

    @Test
    fun a1_connected() {
        val quote = gson.fromJson<BuxMessage<*>>(BODY_CONNECTED, BuxMessage::class.java)
        Assert.assertEquals(MessageType.CONNECTED, quote.type)
    }

    @Test
    fun a2_quote() {
        val quote = gson.fromJson<BuxMessage<*>>(BODY_QUOTE, BuxMessage::class.java)

        Assert.assertEquals(MessageType.QUOTE, quote.type)
    }

    /* ---------- */

    // TODO generic -> Connected

    // TODO non generic -> BuxMessage.typetoken blabla

    /* ---------- */


    @Test
    fun b1_genericConnected() {
        val type = object: TypeToken<BuxMessage<Connected>>(){}.type

        val quote = gson.fromJson<BuxMessage<Connected>>(BODY_CONNECTED, type)

        Assert.assertEquals("b1c30e28-a2b3-4b1b-8bcb-f9be85a50c17", quote.body.sessionId)

        Assert.assertEquals(MessageType.CONNECTED, quote.type)
    }


    @Test
    fun b2_genericQuote() {
        val type = object: TypeToken<BuxMessage<Quote>>(){}.type

        val quote = gson.fromJson<BuxMessage<Quote>>(BODY_QUOTE, type)

        Assert.assertEquals(1.29079, quote.body.currentPrice, 0.0)
        Assert.assertEquals(1582231088710, quote.body.timeStamp)

        Assert.assertEquals(MessageType.QUOTE, quote.type)
    }


}
