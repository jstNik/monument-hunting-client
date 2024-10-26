package com.example.monument_hunting.activities

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.monument_hunting.ui.theme.MonumentHuntingTheme
import com.example.monument_hunting.ui.theme.maps_activity.ComposeTreasureMap

class MapsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            MonumentHuntingTheme {
                ComposeTreasureMap(
                    checkPositionPermission()
                )
            }
        }
    }

    private fun checkPositionPermission(): List<String> {
        val checkCoarseLocation =
            checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION)
        val checkFineLocation =
            checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)
        val permissionsToRequest = mutableListOf<String>()
        if(checkCoarseLocation == PackageManager.PERMISSION_DENIED)
            permissionsToRequest.add(android.Manifest.permission.ACCESS_COARSE_LOCATION)
        if(checkFineLocation == PackageManager.PERMISSION_DENIED)
            permissionsToRequest.add(android.Manifest.permission.ACCESS_FINE_LOCATION)
        return permissionsToRequest
    }

}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MonumentHuntingTheme {

    }
}

