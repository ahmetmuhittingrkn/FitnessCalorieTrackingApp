package com.example.kaloritakip.data.model

data class ExerciseLog(
    val id: String = "",
    val userId: String = "",
    val exerciseName: String = "",
    val sets: Int = 0,
    val reps: Int = 0,
    val caloriesBurned: Int = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val date : String = "",
    val weight: Double = 0.0
)
