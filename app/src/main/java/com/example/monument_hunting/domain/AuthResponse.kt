package com.example.monument_hunting.domain

import com.google.gson.annotations.SerializedName

data class AuthResponse (

    @SerializedName("auth_token")
    var authToken: AuthToken,
    @SerializedName("player")
    var player: Player

)