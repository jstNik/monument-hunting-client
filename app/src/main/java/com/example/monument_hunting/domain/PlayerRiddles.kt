package com.example.monument_hunting.domain

import com.google.gson.annotations.SerializedName


data class PlayerRiddles(
    @SerializedName("player")
    var playerId: Int = 0,
    @SerializedName("riddle")
    var riddleId: Int = 0
)