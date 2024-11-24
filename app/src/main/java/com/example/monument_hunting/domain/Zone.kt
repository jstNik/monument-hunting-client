package com.example.monument_hunting.domain

import com.google.android.gms.maps.model.LatLng

class Zone(

    val id: Int = 0,
    val name: String = "",
    val monument: Monument = Monument(),
    val borders: List<LatLng> = listOf()

)