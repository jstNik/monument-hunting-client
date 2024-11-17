package com.example.monument_hunting.api

import com.example.monument_hunting.domain.ServerData
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface FreeApi {

    companion object {
        const val BASE_URL = "https://monument-hunting-server.onrender.com/"
    }

    @GET("zones/all/")
    suspend fun requestData(
        @Header("Authorization") bearer: String
    ): Response<ServerData>

    @FormUrlEncoded
    @POST("players/auth/refresh/")
    suspend fun refreshToken(
        @Field("refresh_token") refreshToken: String
    ): Response<AuthToken>

    @FormUrlEncoded
    @POST("players/auth/verify/")
    suspend fun verifyToken(
        @Field("access_token") accessToken: String
    ): Response<Void>

    @FormUrlEncoded
    @POST("players/login/")
    suspend fun login(
        @Field("username") username: String,
        @Field("password") password: String
    ): Response<AuthToken>

    @FormUrlEncoded
    @POST("players/signup/")
    suspend fun signup(
        @Field("username") username: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Response<AuthToken>

}