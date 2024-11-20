package com.example.monument_hunting.ui.composables

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.monument_hunting.domain.Zone
import com.google.android.gms.maps.model.JointType
import com.google.maps.android.compose.GoogleMapComposable
import com.google.maps.android.compose.Polygon

@Composable
@GoogleMapComposable
fun ZonePolygon(
    zone: Zone
){

    Polygon(
        zone.coordinates.map { it.latLng },
        fillColor = zone.color.copy(alpha=0.2F),
        strokeJointType = JointType.BEVEL,
        strokeColor = zone.color.copy(alpha = 0.5F),
        tag = zone.name,
    )

}