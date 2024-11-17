package com.example.monument_hunting.domain


import com.google.gson.annotations.SerializedName

data class ServerData(
    @SerializedName("player")
    var player: Player = Player(),
    @SerializedName("player_riddles")
    var playerRiddles: List<PlayerRiddle> = listOf(),
    @SerializedName("riddles")
    var riddles: List<Riddle> = listOf(),
    @SerializedName("zones")
    var zones: List<Zone> = listOf()
)