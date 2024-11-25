package com.example.monument_hunting.ui.composables

import android.app.PendingIntent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.monument_hunting.R
import com.example.monument_hunting.domain.Monument
import com.example.monument_hunting.utils.Data
import com.example.monument_hunting.utils.LocationError
import com.example.monument_hunting.view_models.HomePageViewModel
import com.example.monument_hunting.view_models.LocationViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.LocationSource
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
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
    val catalog by homePageViewModel.catalog.collectAsStateWithLifecycle()
    val bottomSheetState = rememberBottomSheetScaffoldState()
    val cameraPositionState = rememberCameraPositionState {
        this.position = CameraPosition.fromLatLngZoom(
            LatLng(43.771560802932385, 11.254950412763199),
            13F
        )
    }
    var focusOnPlayer by remember {
        mutableStateOf(false)
    }
    LaunchedEffect(focusOnPlayer) {
        if (location.status == Data.Status.Success && focusOnPlayer) {
            location.data?.let {
                cameraPositionState.animate(
                    CameraUpdateFactory.newCameraPosition(
                        CameraPosition.fromLatLngZoom(
                            it, 18F
                        )
                    ),
                    1000
                )
            }
            focusOnPlayer = false
        }
    }
    var noMonumentInZone by remember {
        mutableStateOf(false)
    }

    var cameraModeEnabled by remember {
        mutableStateOf(false)
    }

    var monument: Monument? by remember {
        mutableStateOf(null)
    }
    var cameraErrorMessage by remember {
        mutableStateOf("")
    }
    var cameraSuccessMessage by remember {
        mutableStateOf("")
    }


    LaunchedEffect(cameraErrorMessage) {
        if (cameraErrorMessage != "") {
            bottomSheetState.snackbarHostState.showSnackbar(
                cameraErrorMessage
            )
            cameraErrorMessage = ""
        }
    }
    LaunchedEffect(noMonumentInZone) {
        if (noMonumentInZone) {
            bottomSheetState.snackbarHostState.showSnackbar(
                "No monument in this zone"
            )
            noMonumentInZone = false
        }
    }
    LaunchedEffect(cameraSuccessMessage) {
        if(cameraSuccessMessage != ""){
            bottomSheetState.snackbarHostState.showSnackbar(
                cameraSuccessMessage
            )
            cameraSuccessMessage = ""
        }
    }

    if(cameraModeEnabled) {
        CameraScreen(
            monument,
            location.data,
            onSuccess = { message ->
                cameraModeEnabled = false
                monument?.let {
                    homePageViewModel.postRiddle(it.riddle)
                    cameraSuccessMessage = message
                }

            },
            onFailure = { error ->
                cameraModeEnabled = false
                error?.let{
                    cameraErrorMessage = it
                }
            }
        )
    } else {
        BottomSheetScaffold(
            scaffoldState = bottomSheetState,
            sheetContent = {
                HomePageSheetContent(monument?.riddle, location.data){
                    if(monument?.riddle?.isFound == false && location.status == Data.Status.Success && location.data != null)
                        cameraModeEnabled = true
                    else
                        noMonumentInZone = true
                }
            },
            snackbarHost = { SnackbarHost(hostState = bottomSheetState.snackbarHostState) },
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
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        FloatingActionButton(
                            onClick = { focusOnPlayer = true },
                            modifier = Modifier
                                .padding(16.dp)
                                .size(48.dp),
                            shape = CircleShape
                        ) {

                            Icon(
                                painterResource(R.drawable.my_location),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )

                        }

                        FloatingActionButton(
                            onClick = {
                                if (
                                    monument?.riddle?.isFound == false
                                    && location.status == Data.Status.Success
                                    && location.data != null
                                )
                                    cameraModeEnabled = true
                                else
                                    noMonumentInZone = true
                            },
                            shape = RoundedCornerShape(16.dp),
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Icon(
                                painterResource(R.drawable.photo_camera),
                                contentDescription = null
                            )
                        }
                    }
                }
            ) { paddingValues ->

                GMaps(
                    cameraPositionState,
                    locationViewModel.locationSource,
                    paddingValues,
                    location.error !is LocationError.MissingPermissions,
                )

                when (location.status) {

                    Data.Status.Success -> {

                        if (catalog.status == Data.Status.Success) {
                            val myZone = location.data?.let { l ->
                                catalog.data?.getZoneIn(l)
                            }
                            monument = catalog.data?.let{ data ->
                                val r = myZone?.monument?.riddle
                                if(r?.isFound == true) null else myZone?.monument
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
    locationSource: LocationSource? = null,
    contentPadding: PaddingValues,
    isMyLocationEnabled: Boolean,

    ) {

    val bufferReader =
        LocalContext.current.resources.openRawResource(
            if(isSystemInDarkTheme()) R.raw.dark_maps_style else R.raw.light_maps_style
        ).bufferedReader()
    var res = bufferReader.use { it.readText() }

    bufferReader.close()

    val homePageViewModel = viewModel<HomePageViewModel>()
    val catalog by homePageViewModel.catalog.collectAsStateWithLifecycle()

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
            mapToolbarEnabled = false,
            myLocationButtonEnabled = false
        ),
        locationSource = locationSource,
        properties = MapProperties(
            isMyLocationEnabled = isMyLocationEnabled,
            mapStyleOptions = MapStyleOptions(res),
            latLngBoundsForCameraTarget = LatLngBounds(
                LatLng(43.759474957960556, 11.230407175235507),
                LatLng(43.78624915408978, 11.273451263581741)
            ),
            minZoomPreference = 13F
        )
    ) {

        when (catalog.status) {
            Data.Status.Success -> {

                catalog.data?.regions?.forEach { region ->
                    ZonePolygon(region)
                }
            }

            Data.Status.Loading ->{

            }

            Data.Status.Error ->{

            }

        }

    }
}
