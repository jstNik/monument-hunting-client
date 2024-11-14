package com.example.monument_hunting.di

import android.content.Context
import com.example.monument_hunting.api.AuthTokenManager
import com.example.monument_hunting.api.FreeApi
import com.example.monument_hunting.api.FreeInterceptor
import com.example.monument_hunting.repositories.ApiRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun providesFreeApi(): FreeApi = Retrofit
        .Builder()
        .client(
            OkHttpClient
                .Builder()
                .addInterceptor(FreeInterceptor())
                .build()
        )
        .baseUrl(FreeApi.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create()

    @Provides
    @Singleton
    fun providesBearerTokenManager(@ApplicationContext context: Context) =
        AuthTokenManager(context)

    @Provides
    @Singleton
    fun providesApiRepository(freeApi: FreeApi, authTokenManager: AuthTokenManager) =
        ApiRepository(freeApi, authTokenManager)

}