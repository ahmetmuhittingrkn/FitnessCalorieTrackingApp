package com.example.kaloritakip.data.model

data class FoodResponse(val foods:List<FoodItem>)

data class FoodItem(
    val food_name: String,
    val nf_calories: Double,
    val nf_protein: Double,
    val nf_total_carbohydrate: Double,
    val nf_total_fat: Double,
    val serving_weight_grams: Double,
    val serving_qty: Int,
    val serving_unit: String,
    val mealType:String,
    val mealId:String=""
)