package com.example.monument_hunting.domain.api_domain

import com.google.gson.annotations.SerializedName


data class _PlayerRiddles(
    @SerializedName("player")
    var playerId: Int = 0,
    @SerializedName("riddle")
    var riddleId: Int = 0
){

}