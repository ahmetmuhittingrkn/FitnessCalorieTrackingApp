package com.example.kaloritakip.data.model

data class TurkishFoodItem(
    val foodName: String,
    val calories: Double,
    val protein: Double,
    val carbohydrate: Double,
    val fat: Double,
    val servingWeightGrams: Double,
    val servingQty: Int,
    val servingUnit: String,
    val mealType: String,
    val mealId: String = "",
    val originalFoodName: String
)
