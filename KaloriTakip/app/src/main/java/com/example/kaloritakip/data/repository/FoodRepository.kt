package com.example.kaloritakip.data.repository

import com.example.kaloritakip.data.model.FoodItem
import com.example.kaloritakip.data.model.FoodResponse
import com.example.kaloritakip.data.model.TurkishFoodItem
import com.example.kaloritakip.retrofit.NutritionApiService
import com.example.kaloritakip.util.FoodTranslator
import javax.inject.Inject
import javax.inject.Named

class FoodRepository @Inject constructor(private val nutritionApiService: NutritionApiService) {

    suspend fun searchFood(turkishQuery: String): List<TurkishFoodItem> {
        val englishQuery = convertQueryToEnglish(turkishQuery)
        val response = nutritionApiService.searchFood(mapOf("query" to englishQuery))
        return response.foods.map { it.toTurkishFoodItem() }
    }

    private fun convertQueryToEnglish(turkishQuery: String): String {
        val words = turkishQuery.split(" ")
        val englishWords = words.map { FoodTranslator.toEnglish(it) }
        return englishWords.joinToString(" ")
    }

    private fun FoodItem.toTurkishFoodItem(): TurkishFoodItem {
        return TurkishFoodItem(
            foodName = FoodTranslator.toTurkish(food_name),
            calories = nf_calories,
            protein = nf_protein,
            carbohydrate = nf_total_carbohydrate,
            fat = nf_total_fat,
            servingWeightGrams = serving_weight_grams,
            servingQty = serving_qty,
            servingUnit = FoodTranslator.toTurkish(serving_unit),
            mealType = FoodTranslator.toTurkish(mealType),
            mealId = mealId ?: "",
            originalFoodName = food_name ?: "unknown"
        )
    }

}