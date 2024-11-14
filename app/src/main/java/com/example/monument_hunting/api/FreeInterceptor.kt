package com.example.monument_hunting.api

import com.example.monument_hunting.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response

open class FreeInterceptor: Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        return chain.proceed(
            chain
                .request()
                .newBuilder()
                .addHeader("API-KEY", BuildConfig.SERVER_API_KEY)
                .build()
        )
    }
}