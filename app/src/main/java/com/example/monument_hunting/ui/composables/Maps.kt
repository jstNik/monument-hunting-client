package com.example.monument_hunting.ui.composables

import android.app.PendingIntent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.monument_hunting.R
import com.example.monument_hunting.utils.Data
import com.example.monument_hunting.utils.LocationError
import com.example.monument_hunting.view_models.HomePageViewModel
import com.example.monument_hunting.view_models.LocationViewModel
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState


@Composable
fun RequestPermission(
    permissions: List<String>,
    onResult: (Boolean) -> Unit,
){

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        onResult(results.all { it.value })
    }

    LaunchedEffect(Unit) {
        launcher.launch(permissions.toTypedArray())
    }

    // TODO Explain why your app needs the permission:
    //  https://developer.android.com/training/permissions/requesting#explain


}

@Composable
fun RequestLocationServices(
    intent: PendingIntent,
    onResult: (ActivityResult) -> Unit
){

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { activityResult ->
        onResult(activityResult)
    }

    LaunchedEffect(Unit) {
        launcher.launch(IntentSenderRequest.Builder(intent).build())
    }

}


@Composable
fun ComposeTreasureMap() {


    Scaffold(
        topBar = {
            Row(
                Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primary)) {
                Text(
                    "Monument Hunting",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.padding(start=16.dp, top=8.dp, bottom=8.dp)
                )
            }
        },
        bottomBar = {
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(16.dp)
                    .background(MaterialTheme.colorScheme.primary)) {

            }
        }
    ) { paddingValues ->


        val cameraPositionState = rememberCameraPositionState {
            this.position = CameraPosition.fromLatLngZoom(
                LatLng(43.771560802932385, 11.254950412763199),
                13F
            )
        }
        val locationViewModel = viewModel<LocationViewModel>()
        val location by locationViewModel.location.collectAsStateWithLifecycle()

        GMaps(
            cameraPositionState,
            paddingValues,
            location.error !is LocationError.MissingPermissions,

            )

        when (location.status) {

            Data.Status.Success -> { }
            Data.Status.Loading -> { }
            Data.Status.Error -> {
                when (location.error!!) {

                    is LocationError.MissingPermissions -> {
                        val error = location.error as? LocationError.MissingPermissions
                        if(error != null)
                            RequestPermission(error.missingPermissions!!) { isGranted ->
                                if (isGranted)
                                    locationViewModel.startPositionTracking()
                            }
                    }

                    is LocationError.LocationServicesDisabled -> {
                        val error = location.error as? LocationError.LocationServicesDisabled
                        if (error?.exception != null)
                            RequestLocationServices(error.exception.resolution) { activityResult ->
                                locationViewModel.onResult(activityResult)
                            }
                    }

                    is LocationError.LocationUnreachable -> {
                        Snackbar(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            location.error!!.message?.let {
                                Text(
                                    it,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GMaps(
    cameraPositionState: CameraPositionState,
    contentPadding: PaddingValues,
    isMyLocationEnabled: Boolean
){

    val bufferReader = LocalContext.current.resources.openRawResource(R.raw.maps_style).bufferedReader()
    var res = bufferReader.use{ it.readText() }
    bufferReader.close()
    res = res.format(
        Integer
            .toHexString(MaterialTheme.colorScheme.secondary.toArgb())
            .replaceRange(0..1, "#")
    )
    val homePageViewModel = viewModel<HomePageViewModel>()
    val zoneState by homePageViewModel.zones.collectAsStateWithLifecycle()

    GoogleMap(
        cameraPositionState = cameraPositionState,
        contentPadding = contentPadding,
        googleMapOptionsFactory = {
            GoogleMapOptions()
                .compassEnabled(true)
        },
        uiSettings = MapUiSettings(
            indoorLevelPickerEnabled = false
        ),
        properties = MapProperties(
            isMyLocationEnabled = isMyLocationEnabled,
            mapStyleOptions = MapStyleOptions(res)
        )
    ) {

        when(zoneState.status){
            Data.Status.Success -> {
                zoneState.data!!.forEach {
                    ZonePolygon(
                        it
                    )
                }
            }
            Data.Status.Loading ->
                Toast.makeText(LocalContext.current, "Loading", Toast.LENGTH_LONG).show()

            Data.Status.Error ->
                Toast.makeText(LocalContext.current, "Error", Toast.LENGTH_LONG).show()

        }

    }
}
