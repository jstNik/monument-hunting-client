package com.example.monument_hunting.ui.theme.maps_activity

import android.Manifest
import android.app.Activity.RESULT_OK
import android.app.PendingIntent
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.monument_hunting.utils.Data
import com.example.monument_hunting.utils.LocationError
import com.example.monument_hunting.view_models.LocationViewModel
import com.google.android.gms.location.Priority
import com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState


@Composable
fun RequestPermission(
    permission: String,
    onResult: (Boolean) -> Unit,
){

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        onResult(isGranted)
    }

    LaunchedEffect(Unit) {
        launcher.launch(permission)
    }

    // TODO Explain why your app needs the permission:
    //  https://developer.android.com/training/permissions/requesting#explain


}

@Composable
fun RequestLocationServices(
    intent: PendingIntent,
    onResult: (Boolean) -> Unit
){

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { activityResult ->
        onResult(activityResult.resultCode == RESULT_OK)
    }

    LaunchedEffect(Unit) {
        launcher.launch(
            IntentSenderRequest.Builder(intent).build()
        )
    }

}


@Composable
fun ComposeTreasureMap(
    locationViewModel: LocationViewModel,
) {
    val permission = Manifest.permission.ACCESS_FINE_LOCATION
    val location by locationViewModel.location.collectAsStateWithLifecycle()

    when (location.status) {

        Data.Status.Success -> GMaps(location.data)
        Data.Status.Loading -> GMaps(null)
        Data.Status.Error -> {
            GMaps(null)
            when (location.error!!) {

                is LocationError.MissingPermissions -> {
                    RequestPermission(permission) { isGranted ->
                        if (isGranted)
                            locationViewModel.startPositionTracking()
                    }
                }
                is LocationError.LocationServicesDisabled -> {
                    val error = location.error as? LocationError.LocationServicesDisabled
                    if (error?.resolvableApiException != null)
                        RequestLocationServices(error.resolvableApiException.resolution) { success ->
                            if (success)
                                locationViewModel.startPositionTracking()
                        }
                }
            }
        }
    }
}

@Composable
fun GMaps(
    myPosition: LatLng?
){

    var myMarkerState: MarkerState? = null
    var cameraPositionState = rememberCameraPositionState()
    myPosition?.let{
        myMarkerState = rememberMarkerState(null, it)
        cameraPositionState = rememberCameraPositionState{
            this.position = CameraPosition.fromLatLngZoom(it, 10F)
        }

    }

    GoogleMap(
        cameraPositionState = cameraPositionState,
        modifier = Modifier.fillMaxSize()
    ){
        myMarkerState?.let{
            Marker(
                state = it,
                title = "Me",
                snippet = "Marker on me"
            )
        }
    }

}