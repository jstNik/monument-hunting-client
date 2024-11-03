package com.example.monument_hunting

import android.app.Application
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.MapsInitializer.Renderer.LATEST
import com.google.android.gms.maps.OnMapsSdkInitializedCallback

class MonumentHuntingApp: Application(), OnMapsSdkInitializedCallback {

    override fun onCreate() {
        super.onCreate()
        MapsInitializer.initialize(applicationContext, LATEST, this)
    }

    override fun onMapsSdkInitialized(p0: MapsInitializer.Renderer) { }

}