package com.example.monument_hunting.domain


import com.google.android.gms.maps.model.LatLng
import com.google.gson.annotations.SerializedName

data class Coordinate(
    @SerializedName("latitude")
    var latitude: Double,
    @SerializedName("longitude")
    var longitude: Double
){

    val latLng get() = LatLng(latitude, longitude)

}