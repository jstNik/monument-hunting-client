package com.example.monument_hunting.exceptions

class ApiRequestException(
    val responseCode: Int? = null,
    override val message: String? = null,
    override val cause: Throwable? = null,
) : Exception(message, cause)



