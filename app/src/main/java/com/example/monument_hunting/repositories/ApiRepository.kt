package com.example.monument_hunting.repositories

import com.example.monument_hunting.api.AuthTokenManager
import com.example.monument_hunting.api.FreeApi
import com.example.monument_hunting.domain.Player
import com.example.monument_hunting.domain.ServerData
import com.example.monument_hunting.exceptions.ApiRequestException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject
import kotlin.jvm.Throws

class ApiRepository @Inject constructor(
    private val freeApi: FreeApi,
    private val authTokenManager: AuthTokenManager,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    @Throws(ApiRequestException::class)
    suspend fun requestData(playerId: Int): ServerData {
        val response = call {
            freeApi.requestData(
                "Bearer ${authTokenManager.extractToken().accessToken}",
                playerId
            )
        }
        validateResponse(response)
        return response.body()!!
    }

    @Throws(ApiRequestException::class)
    suspend fun refreshToken(): Player? {
        if(!authTokenManager.isRefreshTokenValid)
            return null
        val response = call{
            freeApi.refreshToken(authTokenManager.extractToken().refreshToken)
        }
        validateResponse(response)
        val auth = response.body()!!
        authTokenManager.saveToken(auth.authToken)
        return auth.player
    }

    @Throws(ApiRequestException::class)
    suspend fun verifyToken(): Player? {
        try {
            if (!authTokenManager.isAccessTokenValid)
                return refreshToken()
            val response = call {
                freeApi.verifyToken(authTokenManager.extractToken().accessToken)
            }
            validateResponse(response)
            return response.body()!!
        } catch (e: Exception){
            throw ApiRequestException(null, e.message, e)
        }
    }

    @Throws(ApiRequestException::class)
    suspend fun loginSignup(signup: Boolean, username: String, email: String, password: String): Player {
        val response = withContext(dispatcher) {
            try {
                if (signup)
                    freeApi.signup(username, email, password)
                else {
                    freeApi.login(username, password)
                }
            }catch (ex: Exception) {
                throw ApiRequestException(null, ex.message, ex)
            }
        }
        validateResponse(response)
        authTokenManager.saveToken(response.body()!!.authToken)
        return response.body()!!.player
    }

    @Throws(ApiRequestException::class)
    private suspend fun <T> call(action: suspend () -> Response<T>): Response<T> {
        if (!authTokenManager.isAccessTokenValid)
            if(refreshToken() == null)
                throw ApiRequestException(null, "Refresh token is not valid")
        return withContext(dispatcher) {
            try {
                action()
            } catch (ex: Exception) {
                throw ApiRequestException(null, ex.message, ex)
            }
        }
    }

    @Throws(ApiRequestException::class)
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