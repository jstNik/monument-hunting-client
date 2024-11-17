package com.example.monument_hunting.domain


import com.google.gson.annotations.SerializedName

data class Player(
    @SerializedName("id")
    var id: Int,
    @SerializedName("username")
    var username: String,
    @SerializedName("zone")
    var zone: Int? = null
)