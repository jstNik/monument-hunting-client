package com.example.monument_hunting.di

import com.example.monument_hunting.api.MonumentHuntingApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
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
    fun providesMonumentHuntingApi(): MonumentHuntingApi = Retrofit
        .Builder()
        .client(
            OkHttpClient
                .Builder()
//                .addInterceptor()
                .build()
        )
        .baseUrl(MonumentHuntingApi.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create()
}