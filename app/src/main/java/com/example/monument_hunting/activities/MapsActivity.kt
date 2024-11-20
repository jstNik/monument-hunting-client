package com.example.monument_hunting.activities

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.monument_hunting.api.FreeApi
import com.example.monument_hunting.domain.Player
import com.example.monument_hunting.ui.theme.MonumentHuntingTheme
import com.example.monument_hunting.ui.composables.ComposeTreasureMap
import com.example.monument_hunting.utils.LocationReceiver
import com.example.monument_hunting.view_models.HomePageViewModel
import com.example.monument_hunting.view_models.LocationViewModel
import com.google.gson.Gson
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
        val player = intent.extras?.getString("player")?.let{
            Gson().fromJson(it, Player::class.java)
        }
        if(player == null){
            Toast.makeText(this, "Something went wrong while retrieving your id", Toast.LENGTH_LONG).show()
            val newIntent = Intent(this, LoginActivity::class.java)
            startActivity(newIntent)
            finish()
        }
        locationReceiver = LocationReceiver(locationViewModel)
        locationViewModel.startPositionTracking()
        homePageViewModel.requestData(player!!.id)
        enableEdgeToEdge()
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

