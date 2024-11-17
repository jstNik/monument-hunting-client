package com.example.monument_hunting.ui.composables

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.monument_hunting.domain.Monument
import com.google.android.gms.maps.model.AdvancedMarkerOptions.CollisionBehavior.REQUIRED_AND_HIDES_OPTIONAL
import com.google.android.gms.maps.model.Marker
import com.google.maps.android.compose.AdvancedMarker
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.Circle
import com.google.maps.android.compose.GoogleMapComposable
import com.google.maps.android.compose.rememberMarkerState

@Composable
@GoogleMapComposable
fun MonumentMarker(
    cameraPositionState: CameraPositionState,
    monument: Monument,
    onMarkerClick: (Marker) -> Boolean = { true }
){

    val markerState = rememberMarkerState(
        position = monument.position
    )
    var clicked by remember{
        mutableStateOf(false)
    }

    if(clicked)
        Circle(
            monument.position,
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