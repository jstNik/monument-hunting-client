package com.example.monument_hunting.domain

import com.google.android.gms.maps.model.LatLng

data class Monument(

    val id: Int = 0,
    val name: String = "",
    val position: LatLng = LatLng(0.0, 0.0),
    val riddle: Riddle = Riddle(),
    val category: String = ""
){

}