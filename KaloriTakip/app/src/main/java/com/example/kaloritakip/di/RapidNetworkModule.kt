package com.example.kaloritakip.di

import com.example.kaloritakip.BuildConfig
import com.example.kaloritakip.retrofit.ExerciseDbApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RapidNetworkModule {

    private const val EXERCISE_DB_BASE_URL = "https://exercisedb.p.rapidapi.com/"
    private const val API_KEY = BuildConfig.rapidApiKey

    @Provides
    @Singleton
    fun provideExerciseDbOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(Interceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("X-RapidAPI-Host", "exercisedb.p.rapidapi.com")
                    .addHeader("X-RapidAPI-Key", API_KEY)
                    .build()
                chain.proceed(request)
            })
            .build()
    }

    @Provides
    @Singleton
    fun provideExerciseDbRetrofit(client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(EXERCISE_DB_BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideExerciseDbApiService(retrofit: Retrofit): ExerciseDbApiService {
        return retrofit.create(ExerciseDbApiService::class.java)
    }
}

