package com.example.monument_hunting.domain

import com.google.gson.annotations.SerializedName

data class AuthToken(

    @SerializedName("access_token")
    var accessToken: String = "",
    @SerializedName("access_expiration")
    var accessExpirationSec: Long = 0,
    @SerializedName("refresh_token")
    var refreshToken: String = "",
    @SerializedName("refresh_expiration")
    var refreshExpirationSec: Long = 0

)