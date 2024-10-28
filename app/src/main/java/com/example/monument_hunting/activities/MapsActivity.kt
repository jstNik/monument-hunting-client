package com.example.monument_hunting.activities

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.monument_hunting.ui.theme.MonumentHuntingTheme
import com.example.monument_hunting.ui.theme.maps_activity.ComposeTreasureMap
import com.example.monument_hunting.view_models.LocationViewModel
import com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY

class MapsActivity : ComponentActivity() {

    private val locationViewModel: LocationViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        locationViewModel.startPositionTracking(PRIORITY_HIGH_ACCURACY, 1000L)
        enableEdgeToEdge()
        setContent {
            MonumentHuntingTheme {
                ComposeTreasureMap(
                    locationViewModel
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        locationViewModel.startPositionTracking()
    }

}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MonumentHuntingTheme {

    }
}

