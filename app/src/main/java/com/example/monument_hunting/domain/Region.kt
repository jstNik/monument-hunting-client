package com.example.monument_hunting.domain

import androidx.compose.ui.graphics.Color
import com.google.android.gms.maps.model.LatLng

data class Region(

    val id: Int = 0,
    val name: String = "",
    val borders: List<LatLng> = listOf(),
    val zones: List<Zone> = listOf(),
    val color: Color = Color(0x00000000)

)