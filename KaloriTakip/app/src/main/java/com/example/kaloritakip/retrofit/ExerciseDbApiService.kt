package com.example.kaloritakip.retrofit

import com.example.kaloritakip.data.model.ExerciseResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface ExerciseDbApiService {

    @GET("exercises/name/{name}")
    suspend fun searchExercises(
        @Path("name") query: String
    ): List<ExerciseResponse>

    @GET("exercises/bodyPart/{bodyPart}")
    suspend fun searchExercisesByBodyPart(
        @Path("bodyPart") bodyPart: String
    ): List<ExerciseResponse>
}