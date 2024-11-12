package com.example.monument_hunting.repositories

import com.example.monument_hunting.api.MonumentHuntingApi
import com.example.monument_hunting.domain.Zone
import com.example.monument_hunting.exceptions.ApiRequestFailed
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.jvm.Throws

class ApiRepository @Inject constructor(
    val api: MonumentHuntingApi,
    val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    @Throws(ApiRequestFailed::class)
    suspend fun requestAllZones(): List<Zone>{
        val response = withContext(dispatcher){
            try {
                api.requestZones()
            } catch (ex: Exception){
                throw ApiRequestFailed(ex.message ?: "", ex)
            }
        }

        if(!response.isSuccessful)
            throw ApiRequestFailed(
                "Response failed with code: ${response.code()}: ${response.message()}"
            )
        if(response.body() == null)
            throw ApiRequestFailed("Api response is null")

        return response.body()!!.toList()
    }


}