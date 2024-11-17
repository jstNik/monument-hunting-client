package com.example.monument_hunting.domain


import com.google.android.gms.maps.model.LatLng
import com.google.gson.annotations.SerializedName

data class Monument(
    @SerializedName("id")
    var id: Int = 0,
    @SerializedName("latitude")
    var latitude: Double = 0.0,
    @SerializedName("longitude")
    var longitude: Double = 0.0,
    @SerializedName("name")
    var name: String = "",
    @SerializedName("zone")
    var zone: Int = 0
){

    val position get() = LatLng(latitude, longitude)

}