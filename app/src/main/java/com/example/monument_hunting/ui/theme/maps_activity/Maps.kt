package com.example.monument_hunting.ui.theme.maps_activity

import android.app.PendingIntent
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.monument_hunting.R
import com.example.monument_hunting.domain.Monument
import com.example.monument_hunting.utils.Data
import com.example.monument_hunting.utils.LocationError
import com.example.monument_hunting.view_models.LocationViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState


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
fun ComposeTreasureMap(
    locationViewModel: LocationViewModel,
) {

    val monuments = listOf(
        Monument(
            "Cattedrale di Santa Maria del Fiore",
            LatLng(43.7731682124964, 11.25586363755426),
            LatLng(43.772935833098465, 11.254733536552338)
        ),
        Monument(
            "Basilica di Santa Croce di Firenze",
            LatLng(43.768663640510596, 11.262236793283773),
            LatLng(43.76867213627099, 11.261855832391811)
        ),
        Monument(
            "Basilica di Santa Maria Novella",
            LatLng(43.774683306146706, 11.249287088408968),
            LatLng(43.77488876070617, 11.249003878924094)
        )
    )


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
            this.position = CameraPosition.fromLatLngZoom(LatLng(0.0, 0.0), 0F)
        }

        val location by locationViewModel.location.collectAsStateWithLifecycle()

        LaunchedEffect(locationViewModel.isLocationRetrieved) {
            if (locationViewModel.isLocationRetrieved) {
                cameraPositionState.animate(
                    CameraUpdateFactory.newCameraPosition(
                        CameraPosition.fromLatLngZoom(
                            LatLng(
                                location.data!!.latitude,
                                location.data!!.longitude
                            ), 15F
                        )
                    ),
                    2000
                )
            }
        }

        GMaps(
            cameraPositionState,
            paddingValues,
            location.error !is LocationError.MissingPermissions,
            monuments,
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
    isMyLocationEnabled: Boolean,
    monuments: List<Monument>
){

    val bufferReader = LocalContext.current.resources.openRawResource(R.raw.maps_style).bufferedReader()
    var res = bufferReader.use{ it.readText() }
    bufferReader.close()
    res = res.format(
        Integer
            .toHexString(MaterialTheme.colorScheme.secondary.toArgb())
            .replaceRange(0..1, "#")
    )

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

        monuments.forEach{ m->
            MonumentMarker(
                cameraPositionState,
                m,
            )
        }

    }
}
