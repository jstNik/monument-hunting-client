package com.example.monument_hunting.utils

import com.google.android.gms.maps.LocationSource

class LocationSourceImpl: LocationSource {

    var callback: LocationSource.OnLocationChangedListener? = null
        private set

    override fun activate(p0: LocationSource.OnLocationChangedListener) {
        callback = p0
    }

    override fun deactivate() {
        callback = null
    }
}