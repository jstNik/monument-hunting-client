package com.example.monument_hunting.domain.api_domain


import com.google.android.gms.maps.model.LatLng
import com.google.gson.annotations.SerializedName

data class _Monument(
    @SerializedName("id")
    var id: Int = 0,
    @SerializedName("latitude")
    var latitude: Double = 0.0,
    @SerializedName("longitude")
    var longitude: Double = 0.0,
    @SerializedName("name")
    var name: String = "",
    @SerializedName("zone")
    var zoneId: Int = 0,
    @SerializedName("category")
    var category: String = ""
){

    val position get() = LatLng(latitude, longitude)

}