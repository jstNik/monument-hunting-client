package com.example.monument_hunting.exceptions

import retrofit2.Response

class ApiRequestException(
    val responseCode: Int? = null,
    override val message: String? = null,
    override val cause: Throwable? = null,
) : Exception(message, cause)



