package com.example.monument_hunting.domain


import androidx.compose.ui.graphics.Color
import com.google.gson.annotations.SerializedName

data class Zone(
    @SerializedName("coordinates")
    var coordinates: List<Coordinate> = listOf(),
    @SerializedName("id")
    var id: Int = 0,
    @SerializedName("name")
    var name: String = "",
    @SerializedName("color")
    var intColor: Int = 0
){
    val color get() = Color(intColor).copy(alpha=0.5F)
}