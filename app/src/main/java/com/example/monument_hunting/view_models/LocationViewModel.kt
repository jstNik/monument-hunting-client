package com.example.monument_hunting.view_models

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.app.Application
import android.content.pm.PackageManager.PERMISSION_DENIED
import android.location.LocationManager
import android.os.Looper
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.example.monument_hunting.utils.Data
import com.example.monument_hunting.utils.LocationError
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationAvailability
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class LocationViewModel(private val application: Application): AndroidViewModel(application) {

    private var _location = MutableStateFlow<Data<LatLng?, LocationError>>(Data.loading())
    val location
        get() = _location.asStateFlow()

    private val fusedLocation =
        LocationServices.getFusedLocationProviderClient(application.applicationContext)

    private var priority: Int = PRIORITY_HIGH_ACCURACY
    private var millis: Long = 1000L

    private val locationCallback = object: LocationCallback(){

        override fun onLocationAvailability(locationAvailability: LocationAvailability) {
            if(!locationAvailability.isLocationAvailable)
                startPositionTracking()
        }

        override fun onLocationResult(position: LocationResult) {
            _location.value = Data.success(
                if (position.lastLocation != null)
                    LatLng(position.lastLocation!!.latitude, position.lastLocation!!.longitude)
                else null
            )
        }
    }

    fun startPositionTracking(){
        startPositionTracking(priority, millis)
    }

    fun startPositionTracking(withPriority: Int, everyMillis: Long){
        priority = withPriority
        millis = everyMillis
        if (application.checkSelfPermission(ACCESS_FINE_LOCATION) == PERMISSION_DENIED) {
            _location.value = Data.error(LocationError.MissingPermissions("Missing Permissions"))
            return
        }

        val requestLocation = LocationRequest
            .Builder(withPriority, everyMillis)
            .build()

        val locationSettings = LocationSettingsRequest
            .Builder()
            .addLocationRequest(requestLocation)
            .build()
        LocationServices
            .getSettingsClient(application.applicationContext)
            .checkLocationSettings(locationSettings)
            .addOnSuccessListener {
                fusedLocation.requestLocationUpdates(requestLocation, locationCallback, Looper.getMainLooper())
            }.addOnFailureListener { exception ->
                _location.value = Data.error(
                    LocationError.LocationServicesDisabled(
                        "Location's services are disabled",
                        if (exception is ResolvableApiException) exception else null
                    )
                )
            }
    }

    fun stopPositionTracking(){
        fusedLocation.removeLocationUpdates(locationCallback)
    }

    override fun onCleared() {
        super.onCleared()
        stopPositionTracking()
    }

}