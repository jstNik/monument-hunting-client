package com.example.monument_hunting.ui.theme.maps_activity

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import com.example.monument_hunting.domain.Monument
import com.google.android.gms.maps.model.AdvancedMarkerOptions
import com.google.android.gms.maps.model.AdvancedMarkerOptions.CollisionBehavior
import com.google.android.gms.maps.model.AdvancedMarkerOptions.CollisionBehavior.REQUIRED_AND_HIDES_OPTIONAL
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PinConfig
import com.google.maps.android.SphericalUtil
import com.google.maps.android.compose.AdvancedMarker
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.Circle
import com.google.maps.android.compose.GoogleMapComposable
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberMarkerState
import kotlin.random.Random

@Composable
@GoogleMapComposable
fun MonumentMarker(
    cameraPositionState: CameraPositionState,
    monument: Monument,
    onMarkerClick: (Marker) -> Boolean = { true }
){

    val markerState = rememberMarkerState(
        position = monument.circleCenter
    )
    var clicked by remember{
        mutableStateOf(false)
    }

    if(clicked)
        Circle(
            monument.circleCenter,
            fillColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.5F),
            radius = 100.0,
            strokeColor = MaterialTheme.colorScheme.tertiary,
        )



//    PinConfig.builder()
//        .setGlyph(
//            PinConfig.Glyph(
//                BitmapDescriptorFactory.fromResource(
//
//                )
//            )
//        )

    AdvancedMarker(
        markerState,
        title = monument.name,
        onClick = {
            clicked = true
            onMarkerClick(it)
            true
        },
        collisionBehavior = REQUIRED_AND_HIDES_OPTIONAL
    )

}