package com.example.monument_hunting.ui.composables

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import com.example.monument_hunting.domain.Riddle
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMapComposable
import com.google.maps.android.compose.MarkerInfoWindow
import com.google.maps.android.compose.rememberMarkerState
import kotlinx.coroutines.launch

@Composable
@GoogleMapComposable
fun RiddleMarker(
    cameraPositionState: CameraPositionState,
    userLocation: LatLng?,
    riddle: Riddle,
    onMarkerClick: (Marker) -> Boolean = { true } // TODO Remove if it will stay unused
){



    val markerState = rememberMarkerState(
//        position = riddle.position
    )
    val scope = rememberCoroutineScope()


//    PinConfig.builder()
//        .setGlyph(
//            PinConfig.Glyph(
//                BitmapDescriptorFactory.fromResource(
//
//                )
//            )
//        )

    MarkerInfoWindow(
        markerState,
        onClick = { m ->
            scope.launch{
                cameraPositionState.animate(
                    CameraUpdateFactory.newCameraPosition(
                        CameraPosition.fromLatLngZoom(
                            m.position, 20F
                        )
                    ),
                    1000
                )
            }
            false
        }
    ){
//        RiddleMarkerInfoWindow(
//            userLocation,
//            riddle.monument.name,
//            riddle.body,
//            riddle.position
//        )
    }

}

