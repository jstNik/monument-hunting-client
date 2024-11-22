package com.example.monument_hunting.domain


import com.google.gson.annotations.SerializedName

data class Player(
    @SerializedName("id")
    var id: Int = 0,
    @SerializedName("username")
    var username: String = ""
)