package com.example.monument_hunting.domain.api_domain


import com.google.android.gms.maps.model.LatLng
import com.google.gson.annotations.SerializedName

data class _Coordinate(
    @SerializedName("latitude")
    var latitude: Double = 0.0,
    @SerializedName("longitude")
    var longitude: Double = 0.0
){

    val latLng get() = LatLng(latitude, longitude)

}