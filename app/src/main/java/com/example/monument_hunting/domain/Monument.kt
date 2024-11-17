package com.example.monument_hunting.domain


import com.google.android.gms.maps.model.LatLng
import com.google.gson.annotations.SerializedName

data class Monument(
    @SerializedName("id")
    var id: Int,
    @SerializedName("latitude")
    var latitude: Double,
    @SerializedName("longitude")
    var longitude: Double,
    @SerializedName("name")
    var name: String,
    @SerializedName("zone")
    var zone: Int
){

    val position get() = LatLng(latitude, longitude)

}