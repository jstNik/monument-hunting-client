package com.example.monument_hunting.view_models

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.app.Application
import android.content.pm.PackageManager.PERMISSION_DENIED
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.lifecycle.AndroidViewModel
import com.example.monument_hunting.utils.Data
import com.example.monument_hunting.utils.LocationError
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject


class LocationViewModel(
    private val application: Application
): AndroidViewModel(application) {

    private var _location = MutableStateFlow<Data<LatLng?, LocationError>>(Data.loading())
    val location
        get() = _location.asStateFlow()

    private val fusedLocation =
        LocationServices.getFusedLocationProviderClient(application.applicationContext)

    private var priority: Int = PRIORITY_HIGH_ACCURACY
    var isLocationRetrieved: Boolean = false
        private set


    fun startPositionTracking(){
        startPositionTracking(priority)
    }

    @SuppressLint("MissingPermission")
    fun startPositionTracking(withPriority: Int){
        priority = withPriority
        if(!havePerms())
            return
        checkLocationSettings()
    }

    @SuppressLint("MissingPermission")
    fun onResult(activityResult: ActivityResult) {
        if (activityResult.resultCode == RESULT_OK) {
            Log.d("RESPONSE", "SUCCESS")
            if (havePerms())
                emitLocation()
        }
    }

    private fun havePerms(): Boolean{
        val fineLocationMissing =
            application.checkSelfPermission(ACCESS_FINE_LOCATION) == PERMISSION_DENIED
        val coarseLocationMissing =
            application.checkSelfPermission(ACCESS_COARSE_LOCATION) == PERMISSION_DENIED
        if (fineLocationMissing || coarseLocationMissing) {
            _location.value = Data.error(
                LocationError.MissingPermissions(
                    "Missing Permissions",
                    buildList {
                        if(fineLocationMissing) add(ACCESS_FINE_LOCATION)
                        if(coarseLocationMissing) add(ACCESS_COARSE_LOCATION)
                    }
                )
            )
            isLocationRetrieved = false
        }
        return !fineLocationMissing && !coarseLocationMissing
    }

    private fun checkLocationSettings(){
        val requestLocation = LocationRequest
            .Builder(priority, 1000)
            .build()

        val locationSettings = LocationSettingsRequest
            .Builder()
            .addLocationRequest(requestLocation)
            .build()
        LocationServices
            .getSettingsClient(application.applicationContext)
            .checkLocationSettings(locationSettings)
            .addOnSuccessListener {
                if(location.value.status != Data.Status.Success) {
                    Log.d("RESPONSE", "SUCCESS LIST")
                    emitLocation()
                }
            }.addOnFailureListener { exception ->
                Log.d("RESPONSE", "ERROR")
                _location.value = Data.error(
                    if (exception is ResolvableApiException)
                    LocationError.LocationServicesDisabled(
                        "Location's services are disabled",
                         exception
                    ) else LocationError.LocationUnreachable("Location can't be reached")
                )
                isLocationRetrieved = false
            }
    }

    @SuppressLint("MissingPermission")
    private fun emitLocation() {
        if (havePerms())
            fusedLocation.getCurrentLocation(priority, null).addOnSuccessListener {
                if (it != null) {
                    _location.value = Data.success(
                        LatLng(it.latitude, it.longitude)
                    )
                    isLocationRetrieved = true
                } else {
                    _location.value = Data.error(LocationError.LocationUnreachable())
                    isLocationRetrieved = false
                }
            }
    }

}