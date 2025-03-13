package com.example.kaloritakip.di

import com.example.kaloritakip.BuildConfig
import com.example.kaloritakip.data.repository.AuthRepository
import com.example.kaloritakip.data.repository.UserRepository
import com.example.kaloritakip.retrofit.NutritionApiService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val BASE_URL = "https://trackapi.nutritionix.com/"
    private const val API_KEY = BuildConfig.nutritionApiKey
    private const val APP_ID = BuildConfig.nutritionAppId

    @Provides
    @Singleton
    fun provideFirebaseAuth() : FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseStore() : FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideAuthRepository(auth: FirebaseAuth) : AuthRepository {
        return AuthRepository(auth)
    }

    @Provides
    @Singleton
    fun provideUserRepository(auth: FirebaseAuth, firestore: FirebaseFirestore): UserRepository {
        return UserRepository(auth, firestore)
    }

    @Provides
    @Singleton
    @Named("nutrition_client")
    fun provideNutritionOkHttpClient(): OkHttpClient {
        val interceptor = Interceptor { chain ->
            val newRequest = chain.request().newBuilder()
                .addHeader("x-app-key", API_KEY)
                .addHeader("x-app-id", APP_ID)
                .addHeader("Content-Type", "application/json")
                .build()
            chain.proceed(newRequest)
        }
        return OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()
    }

    @Provides
    @Singleton
    @Named("nutrition_retrofit")
    fun provideNutritionRetrofit(@Named("nutrition_client") client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideNutritionApiService(@Named("nutrition_retrofit") retrofit: Retrofit): NutritionApiService {
        return retrofit.create(NutritionApiService::class.java)
    }
}