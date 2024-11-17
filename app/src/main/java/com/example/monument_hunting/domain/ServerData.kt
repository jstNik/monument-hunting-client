package com.example.monument_hunting.domain


import com.google.gson.annotations.SerializedName

data class ServerData(
    @SerializedName("player")
    var player: Player,
    @SerializedName("player_riddles")
    var playerRiddles: List<PlayerRiddle>,
    @SerializedName("riddles")
    var riddles: List<Riddle>,
    @SerializedName("zones")
    var zones: List<Zone>
)