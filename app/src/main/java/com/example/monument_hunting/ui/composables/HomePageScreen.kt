package com.example.monument_hunting.ui.composables

import android.app.PendingIntent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collection.mutableVectorOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.monument_hunting.R
import com.example.monument_hunting.domain.Riddle
import com.example.monument_hunting.domain.ServerData
import com.example.monument_hunting.domain.Zone
import com.example.monument_hunting.exceptions.ApiRequestException
import com.example.monument_hunting.utils.Data
import com.example.monument_hunting.utils.LocationError
import com.example.monument_hunting.view_models.HomePageViewModel
import com.example.monument_hunting.view_models.LocationViewModel
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.LocationSource
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.PolyUtil
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState


@Composable
fun RequestPermission(
    permissions: List<String>,
    onResult: (Boolean) -> Unit,
) {

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
) {

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { activityResult ->
        onResult(activityResult)
    }

    LaunchedEffect(Unit) {
        launcher.launch(IntentSenderRequest.Builder(intent).build())
    }

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComposeTreasureMap() {

    val locationViewModel = viewModel<LocationViewModel>()
    val homePageViewModel = viewModel<HomePageViewModel>()
    val location by locationViewModel.location.collectAsStateWithLifecycle()
    val serverData by homePageViewModel.serverData.collectAsStateWithLifecycle()
    val bottomSheetState = rememberBottomSheetScaffoldState()
    var cameraModeEnabled by remember{
        mutableStateOf(false)
    }
    var riddle: Riddle? by remember {
        mutableStateOf(null)
    }
    var cameraError by remember {
        mutableStateOf(false)
    }

    if(cameraModeEnabled)
        CameraScreen(
            riddle,
            location.data,
            onSuccess = {
                cameraModeEnabled = false
                riddle?.let {
                    homePageViewModel.postRiddle(it.id)
                }
            },
            onFailure = {
                cameraModeEnabled = false
                cameraError = true
            }
        )
    else {
        BottomSheetScaffold(
            scaffoldState = bottomSheetState,
            sheetContent = {
                HomePageSheetContent(riddle, location.data)
            },
            sheetPeekHeight = 128.dp
        ) { padding ->

            val layoutDirection = LocalLayoutDirection.current
            val winIns = WindowInsets(
                padding.calculateLeftPadding(layoutDirection),
                padding.calculateTopPadding(),
                padding.calculateRightPadding(layoutDirection),
                padding.calculateBottomPadding()
            )

            Scaffold(
                contentWindowInsets = winIns,
                topBar = { },
                floatingActionButton = {
                    FloatingActionButton(
                        onClick = {
                            cameraModeEnabled = true
                        },
                        shape = RoundedCornerShape(16.dp),

                        ) {
                        Icon(
                            painterResource(R.drawable.photo_camera),
                            contentDescription = null
                        )
                    }
                }
            ) { paddingValues ->


                val cameraPositionState = rememberCameraPositionState {
                    this.position = CameraPosition.fromLatLngZoom(
                        LatLng(43.771560802932385, 11.254950412763199),
                        13F
                    )
                }

                GMaps(
                    cameraPositionState,
                    location.data,
                    serverData,
                    locationViewModel.locationSource,
                    paddingValues,
                    location.error !is LocationError.MissingPermissions,
                )

                when (location.status) {

                    Data.Status.Success -> {

                        if (serverData.status == Data.Status.Success) {
                            val myZone = location.data?.let { l ->
                                serverData.data?.let { data ->
                                    data.zones.find { zone ->
                                        val polygon = zone.coordinates.map {
                                            it.latLng
                                        }
                                        PolyUtil.containsLocation(l, polygon, true)
                                    }

                                }
                            }

                            val riddlesCompleted = serverData.data?.let { data ->
                                data.riddles.filter { r ->
                                    data.playerRiddles.any { it.riddleId == r.id }
                                }
                            } ?: listOf()

                            riddle = serverData.data?.riddles?.find {
                                it.monument.zoneId == myZone?.id && !riddlesCompleted.contains(it)
                            }
                        }
                    }
                    Data.Status.Loading -> {}
                    Data.Status.Error -> {
                        when (location.error!!) {

                            is LocationError.MissingPermissions -> {
                                val error = location.error as? LocationError.MissingPermissions
                                if (error != null)
                                    RequestPermission(error.missingPermissions!!) { isGranted ->
                                        if (isGranted)
                                            locationViewModel.startPositionTracking()
                                    }
                            }

                            is LocationError.LocationServicesDisabled -> {
                                val error =
                                    location.error as? LocationError.LocationServicesDisabled
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
    }
}

@Composable
fun GMaps(
    cameraPositionState: CameraPositionState,
    playerLocation: LatLng?,
    serverData: Data<ServerData, ApiRequestException>,
    locationSource: LocationSource? = null,
    contentPadding: PaddingValues,
    isMyLocationEnabled: Boolean,

) {

    val bufferReader =
        LocalContext.current.resources.openRawResource(R.raw.maps_style).bufferedReader()
    var res = bufferReader.use { it.readText() }
    bufferReader.close()
    res = res.format(
        Integer
            .toHexString(MaterialTheme.colorScheme.background.toArgb())
            .replaceRange(0..1, "#")
    )
    val homePageViewModel = viewModel<HomePageViewModel>()
    val serverData by homePageViewModel.serverData.collectAsStateWithLifecycle()

    GoogleMap(
        cameraPositionState = cameraPositionState,
        contentPadding = contentPadding,
        googleMapOptionsFactory = {
            GoogleMapOptions()
                .compassEnabled(true)
        },
        uiSettings = MapUiSettings(
            indoorLevelPickerEnabled = false,
            zoomControlsEnabled = false,
            mapToolbarEnabled = false
        ),
        locationSource = locationSource,
        properties = MapProperties(
            isMyLocationEnabled = isMyLocationEnabled,
            mapStyleOptions = MapStyleOptions(res),
            latLngBoundsForCameraTarget = LatLngBounds(
                LatLng(43.759474957960556, 11.230407175235507),
                LatLng(43.78624915408978, 11.273451263581741)
            )
        )
    ) {

        when (serverData.status) {
            Data.Status.Success -> {

                serverData.data!!.zones.forEach { zone ->
                    ZonePolygon(
                        zone,
                        serverData.data!!.regions.find{ it.id == zone.regionId }?.color ?:
                        MaterialTheme.colorScheme.surface,
                        serverData.data!!.playerRiddles.find {
                            it.riddleId == serverData.data!!.riddles.find { r ->
                                r.monument.zoneId == zone.id
                            }?.id
                        } == null
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
