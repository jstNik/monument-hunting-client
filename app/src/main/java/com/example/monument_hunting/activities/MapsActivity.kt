package com.example.monument_hunting.activities

import android.annotation.SuppressLint
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import com.example.monument_hunting.ui.theme.MonumentHuntingTheme
import com.example.monument_hunting.ui.theme.maps_activity.ComposeTreasureMap
import com.example.monument_hunting.utils.LocationReceiver
import com.example.monument_hunting.view_models.LocationViewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MapsActivity : ComponentActivity() {

    private val locationViewModel: LocationViewModel by viewModels()
    private var locationReceiver: LocationReceiver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        locationReceiver = LocationReceiver(locationViewModel)
        locationViewModel.startPositionTracking()
        setContent {
            MonumentHuntingTheme {

                ComposeTreasureMap(locationViewModel)

            }
        }
    }

    override fun onResume() {
        super.onResume()
        locationReceiver?.isProcessing = false
        registerReceiver(
            locationReceiver,
            IntentFilter("android.location.PROVIDERS_CHANGED")
        )
    }

    override fun onPause() {
        super.onPause()
        locationReceiver?.isProcessing = true
        unregisterReceiver(locationReceiver)
    }



}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MonumentHuntingTheme {

    }
}

