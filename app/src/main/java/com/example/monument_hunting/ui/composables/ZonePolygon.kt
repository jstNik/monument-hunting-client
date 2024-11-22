package com.example.monument_hunting.ui.composables

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.monument_hunting.domain.Zone
import com.google.android.gms.maps.model.JointType
import com.google.maps.android.compose.GoogleMapComposable
import com.google.maps.android.compose.Polygon

@Composable
@GoogleMapComposable
fun ZonePolygon(
    zone: Zone,
    color: Color,
    fill: Boolean
){

    Polygon(
        zone.coordinates.map { it.latLng },
        fillColor = if (fill) color.copy(alpha=0.2F) else color.copy(alpha = 0F),
        strokeJointType = JointType.BEVEL,
        strokeColor = color.copy(alpha = 0.5F),
        tag = zone.name,
    )

}