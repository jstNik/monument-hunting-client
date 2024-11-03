package com.example.monument_hunting.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.monument_hunting.view_models.LocationViewModel

class LocationReceiver(
    private val locationViwModel: LocationViewModel?
): BroadcastReceiver() {

    var isProcessing: Boolean = false

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action?.equals("android.location.PROVIDERS_CHANGED") == true
            && !isProcessing
        ) {
            isProcessing = true
            Log.d("RESPONSE", "RECEIVER: $this")
            locationViwModel?.startPositionTracking()
        }
    }
}