package com.example.monument_hunting.domain


import com.google.gson.annotations.SerializedName

data class Riddle(
    @SerializedName("body")
    var body: String,
    @SerializedName("id")
    var id: Int,
    @SerializedName("latitude")
    var latitude: Double,
    @SerializedName("longitude")
    var longitude: Double,
    @SerializedName("monument")
    var monument: Monument,
    @SerializedName("zone")
    var zone: Int
)