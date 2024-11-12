package com.example.monument_hunting.exceptions

class ApiRequestFailed(
    override val message: String?,
    override val cause: Throwable?
) : Exception(message, cause) {

    constructor(message: String): this(message, null)
    constructor(cause: Throwable): this(null, cause)
    constructor(): this(null, null)

}