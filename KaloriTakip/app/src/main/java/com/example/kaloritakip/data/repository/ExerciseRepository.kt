package com.example.kaloritakip.data.repository

import android.icu.text.SimpleDateFormat
import com.example.kaloritakip.data.model.ExerciseLog
import com.example.kaloritakip.data.model.ExerciseResponse
import com.example.kaloritakip.retrofit.ExerciseDbApiService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class ExerciseRepository @Inject constructor(
    private val apiService: ExerciseDbApiService,
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {
    suspend fun searchExercises(query: String): Result<List<ExerciseResponse>> {
        return try {
            val response = apiService.searchExercises(query)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun searchExercisesByBodyPart(bodyPart: String): Result<List<ExerciseResponse>> {
        return try {
            val response = apiService.searchExercisesByBodyPart(bodyPart)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addExerciseLog(exerciseLog: ExerciseLog): Result<Unit> {
        return try {
            val userId = auth.currentUser?.uid ?: return Result.failure(Exception("User not logged in"))
            val docRef = firestore.collection("users").document(userId)
                .collection("exercise_logs").document()

            val logWithId = exerciseLog.copy(id = docRef.id)

            docRef.set(logWithId).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserExercises(): Result<List<ExerciseLog>> {
        return try {
            val userId = auth.currentUser?.uid ?: return Result.failure(Exception("User not logged in"))
            val snapshot = firestore.collection("users").document(userId)
                .collection("exercise_logs")
                .orderBy("timestamp")
                .get()
                .await()

            val exercises = snapshot.toObjects(ExerciseLog::class.java)

            val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val todayExercises = exercises.filter { it.date == today }

            Result.success(todayExercises)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteExerciseLog(exerciseId: String): Result<Unit> {
        return try {
            val userId = auth.currentUser?.uid ?: return Result.failure(Exception("User not logged in"))
            firestore.collection("users").document(userId)
                .collection("exercise_logs").document(exerciseId)
                .delete()
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}