package com.example.monument_hunting.domain


import android.annotation.SuppressLint
import android.graphics.Color.parseColor
import androidx.compose.ui.graphics.Color
import com.google.gson.annotations.SerializedName
import kotlin.random.Random

data class Region(
    @SerializedName("id")
    var id: Int = 0,
    @SerializedName("color")
    var hexColor: String = "",
    @SerializedName("coordinates")
    var coordinates: List<Coordinate> = listOf(),
    @SerializedName("name")
    var name: String = ""
){

    val color @SuppressLint("Range")
    get() = try{
            Color(parseColor(hexColor))
        } catch (_: IllegalArgumentException){
            Color((Random.nextDouble() * 0xFFFFFF).toLong())
        }

}