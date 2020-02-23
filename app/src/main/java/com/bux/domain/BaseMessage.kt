package com.bux.domain

import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName

/**
 * TODO Describe class functionality.
 *
 * Created by Zenox on 21-2-2020 at 11:34.
 */
data class MessageContainer(
    val body: JsonElement,

    @SerializedName("t")
    val type: MessageType?
)

enum class MessageType {

    @SerializedName("connect.connected")
    CONNECTED,

    @SerializedName("trading.quote")
    QUOTE

}


/*


{
	"body": {
		"userId": "bb0cda2b-a10e-4ed3-ad5a-0f82b4c152c4",
		"sessionId": "b1c30e28-a2b3-4b1b-8bcb-f9be85a50c17",
		"time": 1582285348913
	},
	"t": "connect.connected"
}


{
	"body": {
		"securityId": "sb26502",
		"currentPrice": "1.29079",
		"timeStamp": 1582231088710
	},
	"t": "trading.quote"
}





 */