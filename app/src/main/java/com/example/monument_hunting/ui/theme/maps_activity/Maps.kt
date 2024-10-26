package com.example.monument_hunting.ui.theme.maps_activity

import android.annotation.SuppressLint
import android.location.LocationManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@SuppressLint("MissingPermission")
@Composable
fun ComposeTreasureMap(
    permissionsMissing: List<String>
){

    val permissionGranted = remember {
        mutableStateListOf<String>()
    }
    val permissionDenied = remember {
        mutableStateListOf<String>()
    }
    var pos by remember {
        mutableStateOf<LatLng?>(null)
    }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        for(result in results){
            if(result.value)
                permissionGranted.add(result.key)
            else
                permissionDenied.add(result.key)
        }
    }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.Main) {
            Log.d("Launched", "Effect")
        }
        if(permissionsMissing.isNotEmpty())
            launcher.launch(permissionsMissing.toTypedArray())
    }

    GMaps(pos)

    if(permissionGranted == permissionsMissing) {
        val fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(LocalContext.current)
        fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
            location.let{
                pos = LatLng(it.latitude, it.longitude)
            }
        }
    }

}

@Composable
fun GMaps(
    position: LatLng?
){
    var myMarkerState: MarkerState? = null
    var cameraPositionState = rememberCameraPositionState()
    position?.let{
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