package com.example.monument_hunting.domain

import com.example.monument_hunting.domain.api_domain._Player
import com.google.gson.annotations.SerializedName

data class AuthResponse (

    @SerializedName("auth_token")
    var authToken: AuthToken,
    @SerializedName("player")
    var player: _Player

)