package com.example.monument_hunting.di

import android.content.Context
import com.example.monument_hunting.api.AuthTokenManager
import com.example.monument_hunting.api.FreeApi
import com.example.monument_hunting.api.FreeInterceptor
import com.example.monument_hunting.repositories.ApiRepository
import com.example.monument_hunting.repositories.MediaPipeRepository
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.core.Delegate
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.imageclassifier.ImageClassifier
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

    @Provides
    @Singleton
    fun providesMediaPipeModel(@ApplicationContext context: Context) =
        ImageClassifier.createFromOptions(
            context,
            ImageClassifier.ImageClassifierOptions.builder()
                .setBaseOptions(
                    BaseOptions.builder()
                        .setDelegate(Delegate.GPU)
                        .setModelAssetPath("model_3_float16.tflite")
                        .build()
                )
                .setMaxResults(10)
                .setRunningMode(RunningMode.IMAGE)
                .build()
        )

    @Provides
    @Singleton
    fun providesMediaPipeRepository(imageClassifier: ImageClassifier) =
        MediaPipeRepository(imageClassifier)

}