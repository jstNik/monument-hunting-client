package com.example.monument_hunting.utils

class Data<T, E> private constructor(
    val data: T? = null,
    val error: E? = null,
    val status: Status
) {

    enum class Status {
        Success, Loading, Error
    }

    companion object{
        fun <T, E> success(data: T?) = Data<T, E>(data, null, Status.Success)
        fun <T, E> loading() = Data<T, E>(status=Status.Loading)
        fun <T, E> error(error: E) = Data<T, E>(null, error, Status.Error)
    }


}