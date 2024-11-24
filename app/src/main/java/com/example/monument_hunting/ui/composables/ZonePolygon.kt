package com.example.monument_hunting.ui.composables

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.monument_hunting.domain.Region
import com.google.android.gms.maps.model.JointType
import com.google.maps.android.compose.GoogleMapComposable
import com.google.maps.android.compose.Polygon

@Composable
@GoogleMapComposable
fun ZonePolygon(
    region: Region
){

    Polygon(
        region.borders,
        fillColor = Color.Transparent,
        strokeColor = region.color.copy(alpha = 1F),
        strokeJointType = JointType.ROUND,
        strokeWidth = 5F
    )

    region.zones.forEach { zone ->

        val fill: Boolean = zone.monument.riddle.isFound

        Polygon(
            zone.borders,
            fillColor = if (fill) region.color.copy(alpha = 0.2F) else region.color.copy(alpha = 0F),
            strokeJointType = JointType.ROUND,
            strokeColor = region.color.copy(alpha = 1F),
            strokeWidth = 5F,
            tag = zone.name
        )
    }

}