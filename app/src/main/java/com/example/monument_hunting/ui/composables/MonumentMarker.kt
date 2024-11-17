package com.example.monument_hunting.ui.composables

import androidx.compose.runtime.Composable
import com.example.monument_hunting.domain.Monument
import com.example.monument_hunting.domain.Riddle
import com.google.android.gms.maps.model.AdvancedMarkerOptions.CollisionBehavior.REQUIRED_AND_HIDES_OPTIONAL
import com.google.android.gms.maps.model.Marker
import com.google.maps.android.compose.AdvancedMarker
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMapComposable
import com.google.maps.android.compose.rememberMarkerState

@Composable
@GoogleMapComposable
fun MonumentMarker(
    cameraPositionState: CameraPositionState,
    riddle: Riddle,
    onMarkerClick: (Marker) -> Boolean = { true }
){

    val markerState = rememberMarkerState(
        position = riddle.position
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
        onClick = {
            onMarkerClick(it)
            true
        },
        collisionBehavior = REQUIRED_AND_HIDES_OPTIONAL
    )

}