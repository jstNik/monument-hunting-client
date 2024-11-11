package com.example.monument_hunting.activities

import android.content.IntentFilter
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import com.example.monument_hunting.api.MonumentHuntingApi
import com.example.monument_hunting.ui.theme.MonumentHuntingTheme
import com.example.monument_hunting.ui.theme.maps_activity.ComposeTreasureMap
import com.example.monument_hunting.utils.LocationReceiver
import com.example.monument_hunting.view_models.LocationViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MapsActivity : ComponentActivity() {

    private val locationViewModel: LocationViewModel by viewModels()
    private var locationReceiver: LocationReceiver? = null

    @Inject
    lateinit var api: MonumentHuntingApi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        locationReceiver = LocationReceiver(locationViewModel)
        locationViewModel.startPositionTracking()
        lifecycleScope.launch(Dispatchers.IO) {
            val res = api.requestZones()
        }
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

