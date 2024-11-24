package com.example.monument_hunting.utils

import com.google.android.gms.common.api.ResolvableApiException

sealed class LocationError(open val message: String?) {

    class MissingPermissions(
        override val message: String?,
        val missingPermissions: List<String>?
    ): LocationError(message){
        constructor() : this(null, null)
    }

    class LocationServicesDisabled(
        override val message: String?,
        val exception: ResolvableApiException?,
    ) : LocationError(message) {
        constructor(exception: ResolvableApiException) : this(null, exception)
        constructor(): this(null, null)
    }

    class LocationUnreachable(
        override val message: String?
    ): LocationError(message){
        constructor(): this(null)
    }

}