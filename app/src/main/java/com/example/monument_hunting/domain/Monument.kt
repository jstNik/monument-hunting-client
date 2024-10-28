package com.example.monument_hunting.domain

import com.google.android.gms.maps.model.LatLng


data class Monument(
    val name: String,
    val coordinates: LatLng,
    val found: Boolean = false,
    ) {
}