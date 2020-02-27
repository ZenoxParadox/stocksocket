package com.bux.network.rest

import androidx.annotation.StringRes
import com.bux.R
import com.google.gson.annotations.SerializedName

/**
 * Bux specific HTTP errors
 */
enum class ErrorType(@StringRes val text: Int) {

    @SerializedName("TRADING_002")
    UNEXPECTED(R.string.error_unexpected),

    @SerializedName("AUTH_007")
    INVALIDTOKEN(R.string.error_token_invalid),

    @SerializedName("AUTH_014")
    ACCESS_PERMISSION(R.string.error_access_permission),

    @SerializedName("AUTH_009")
    AUTH_HEADER(R.string.error_missing_auth_header),

    @SerializedName("AUTH_008")
    EXPIRED(R.string.error_session_expired)

}