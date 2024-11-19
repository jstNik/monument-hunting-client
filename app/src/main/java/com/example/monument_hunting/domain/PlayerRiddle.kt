package com.example.monument_hunting.domain


import com.google.gson.annotations.SerializedName

data class PlayerRiddle(
    @SerializedName("id")
    var id: Int = 0,
    @SerializedName("is_completed")
    var isCompleted: Boolean = false,
    @SerializedName("player")
    var player: Int = 0,
    @SerializedName("riddle")
    var riddle: Riddle = Riddle()
)