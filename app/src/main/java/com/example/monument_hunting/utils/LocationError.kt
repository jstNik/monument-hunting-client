package com.example.monument_hunting.utils

import com.google.android.gms.common.api.ResolvableApiException

sealed class LocationError(open val message: String?) {

    class MissingPermissions(
        override val message: String?
    ): LocationError(message){
        constructor() : this(null)
    }

    class LocationServicesDisabled(
        override val message: String?,
        val resolvableApiException: ResolvableApiException?,
    ) : LocationError(message) {
        constructor(resolvableApiException: ResolvableApiException?) : this(
            null,
            resolvableApiException
        )
        constructor(): this(null, null)
    }
}