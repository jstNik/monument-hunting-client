package com.example.monument_hunting.api

import com.example.monument_hunting.domain.ZoneList
import retrofit2.Response
import retrofit2.http.GET

interface MonumentHuntingApi {

    companion object {
        const val BASE_URL = "https://monument-hunting-server.onrender.com/"
    }

    @GET("zones/all")
    suspend fun requestZones(): Response<ZoneList>


}