package com.bux.network.realtime

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.junit.Assert
import org.junit.Test

/**
 * Test to show how the messages should be deserialized
 */
class BuxMessageTest {

    private val gson: Gson = GsonBuilder().create()

    private val BODY_CONNECTED = "{ \"body\": { \"userId\": \"bb0cda2b-a10e-4ed3-ad5a-0f82b4c152c4\", \"sessionId\":\"b1c30e28-a2b3-4b1b-8bcb-f9be85a50c17\", \"time\": 1582285348913 }, \"t\": \"connect.connected\" }"

    private val BODY_QUOTE = "{ \"body\": { \"securityId\": \"sb26502\", \"currentPrice\": \"1.29079\", \"timeStamp\": 1582231088710 }, \"t\": \"trading.quote\" }"

    @Test
    fun `a1 - should read the CONNECTED type from the base object`() {
        val quote = gson.fromJson<BuxMessage>(BODY_CONNECTED, BuxMessage::class.java)
        Assert.assertEquals(MessageType.CONNECTED, quote.type)
    }

    @Test
    fun `a2 - should read the QUOTE type from the base object`() {
        val quote = gson.fromJson<BuxMessage>(BODY_QUOTE, BuxMessage::class.java)

        Assert.assertEquals(MessageType.QUOTE, quote.type)
    }

    /* ---------- */

    @Test
    fun `b1 - should read CONNECTED details from the generic object`() {
        val message = gson.fromJson<BuxMessage>(BODY_CONNECTED, BuxMessage::class.java)

        val connected = message.getConnected()

        Assert.assertEquals("b1c30e28-a2b3-4b1b-8bcb-f9be85a50c17", connected!!.sessionId)
        Assert.assertEquals(MessageType.CONNECTED, message.type)
    }

    @Test
    fun `b2 - should read QUOTE details from the generic object`() {
        val message = gson.fromJson<BuxMessage>(BODY_QUOTE, BuxMessage::class.java)
        val quote = message.getQuote()

        Assert.assertEquals(1.29079, quote!!.currentPrice, 0.0)
        Assert.assertEquals(1582231088710, quote!!.timeStamp)

        Assert.assertEquals(MessageType.QUOTE, message.type)
    }

}