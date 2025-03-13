package com.example.kaloritakip.retrofit

import com.example.kaloritakip.data.model.FoodResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface NutritionApiService {

    @POST("v2/natural/nutrients")
    suspend fun searchFood(@Body query: Map<String, String>): FoodResponse

}