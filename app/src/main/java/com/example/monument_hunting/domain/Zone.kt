package com.example.monument_hunting.domain


import com.google.gson.annotations.SerializedName

data class Zone(
    @SerializedName("coordinates")
    var coordinates: List<Coordinate> = listOf(),
    @SerializedName("id")
    var id: Int = 0,
    @SerializedName("name")
    var name: String = ""
)