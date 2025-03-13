package com.example.kaloritakip.data.model

data class ExerciseResponse(
    val id: String,
    val name: String,
    val bodyPart: String,
    val equipment: String,
    val gifUrl: String,
    val target: String,
    val secondaryMuscles: List<String>,
    val instructions: List<String>
)