package com.example.monument_hunting.activities

import android.content.IntentFilter
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.monument_hunting.api.FreeApi
import com.example.monument_hunting.ui.theme.MonumentHuntingTheme
import com.example.monument_hunting.ui.composables.ComposeTreasureMap
import com.example.monument_hunting.utils.LocationReceiver
import com.example.monument_hunting.view_models.HomePageViewModel
import com.example.monument_hunting.view_models.LocationViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MapsActivity : AppCompatActivity() {

    private val locationViewModel: LocationViewModel by viewModels()
    private val homePageViewModel: HomePageViewModel by viewModels()
    private var locationReceiver: LocationReceiver? = null

    @Inject
    lateinit var api: FreeApi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        locationReceiver = LocationReceiver(locationViewModel)
        locationViewModel.startPositionTracking()
        homePageViewModel.requestZones()

        setContent {
            MonumentHuntingTheme {
                ComposeTreasureMap()

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

