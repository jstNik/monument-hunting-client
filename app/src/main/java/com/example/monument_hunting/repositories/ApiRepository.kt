package com.example.monument_hunting.repositories

import com.example.monument_hunting.api.AuthTokenManager
import com.example.monument_hunting.api.FreeApi
import com.example.monument_hunting.domain.ServerData
import com.example.monument_hunting.exceptions.ApiRequestException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject
import kotlin.jvm.Throws

class ApiRepository @Inject constructor(
    val freeApi: FreeApi,
    val authTokenManager: AuthTokenManager,
    val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    @Throws(ApiRequestException::class)
    suspend fun requestAllZones(): ServerData {
        val response = withContext(dispatcher) {
            try {
                freeApi.requestData("Bearer ${authTokenManager.extractToken().accessToken}")
            } catch (ex: Exception) {
                throw ApiRequestException(null, ex.message, ex)
            }
        }
        validateResponse(response)
        return response.body()!!
    }

    suspend fun refreshToken(): Boolean{
        if(!authTokenManager.isRefreshTokenValid)
            return false
        val response = call{
            freeApi.refreshToken(authTokenManager.extractToken().refreshToken)
        }
        validateResponse(response)
        val token = response.body()!!
        authTokenManager.saveToken(token)
        return true
    }

    suspend fun verifyToken(): Boolean {
        try {
            if (!authTokenManager.isAccessTokenValid)
                return refreshToken()
            val response = call {
                freeApi.verifyToken(authTokenManager.extractToken().accessToken)
            }
            return response.isSuccessful
        } catch (e: Exception){
            throw ApiRequestException(null, e.message, e)
        }
    }

    suspend fun loginSignup(signup: Boolean, username: String, email: String, password: String) {
        val response = call {
            if (signup)
                freeApi.signup(username, email, password)
            else {
                freeApi.login(username, password)
            }
        }
        validateResponse(response)
        authTokenManager.saveToken(response.body()!!)
    }


    private suspend fun <T> call(action: suspend () -> Response<T>): Response<T> {
        return withContext(dispatcher) {
            try {
                action()
            } catch (ex: Exception) {
                throw ApiRequestException(null, ex.message, ex)
            }
        }
    }

    private fun <T> validateResponse(response: Response<T>) {
        if (!response.isSuccessful)
            throw ApiRequestException(
                response.code(),
                response.errorBody()?.string() ?: response.message(),
                null
            )
        if (response.body() == null)
            throw ApiRequestException(
                response.code(),
                response.errorBody()?.string() ?: response.message(),
                null
            )
    }
}