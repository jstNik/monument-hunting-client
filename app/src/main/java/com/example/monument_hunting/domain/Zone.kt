package com.example.monument_hunting.domain


import android.annotation.SuppressLint
import android.graphics.Color.parseColor
import androidx.compose.ui.graphics.Color
import com.google.gson.annotations.SerializedName
import kotlin.random.Random
import kotlin.uuid.Uuid.Companion.random

data class Zone(
    @SerializedName("coordinates")
    var coordinates: List<Coordinate> = listOf(),
    @SerializedName("id")
    var id: Int = 0,
    @SerializedName("name")
    var name: String = "",
    @SerializedName("color")
    var hexColor: String = ""
){
    val color @SuppressLint("Range")
    get() = try{
        Color(parseColor(hexColor)).copy(alpha=0.5F)
    } catch (_: IllegalArgumentException){
        Color((Random.nextDouble()*0xFFFFFF).toLong())
    }
}