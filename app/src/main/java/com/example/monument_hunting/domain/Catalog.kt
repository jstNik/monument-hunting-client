package com.example.monument_hunting.domain

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.PolyUtil

data class Catalog(

    val player: Player = Player(),
    val regions: List<Region> = listOf()

){

    fun getZoneIn(location: LatLng): Zone? {
        val myRegion = regions.find {
            PolyUtil.containsLocation(location, it.borders, true)
        } ?: return null
        val myZone = myRegion.zones.find {
            PolyUtil.containsLocation(location, it.borders, true)
        } ?: return null
        return myZone
    }

}