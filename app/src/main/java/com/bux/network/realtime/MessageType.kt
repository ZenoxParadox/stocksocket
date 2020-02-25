package com.bux.network.realtime

import com.google.gson.annotations.SerializedName

enum class MessageType {

    @SerializedName("connect.connected")
    CONNECTED,

    @SerializedName("trading.quote")
    QUOTE,

    NONE

}
