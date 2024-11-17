package com.example.monument_hunting.domain


import com.google.gson.annotations.SerializedName

data class PlayerRiddle(
    @SerializedName("id")
    var id: Int,
    @SerializedName("is_completed")
    var isCompleted: Boolean,
    @SerializedName("player")
    var player: Int,
    @SerializedName("riddle")
    var riddle: Riddle
)