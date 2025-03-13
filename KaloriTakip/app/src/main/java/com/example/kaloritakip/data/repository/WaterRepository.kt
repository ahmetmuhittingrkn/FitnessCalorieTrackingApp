package com.example.kaloritakip.data.repository

import android.util.Log
import com.example.kaloritakip.data.model.WaterState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class WaterRepository @Inject constructor(
     private val firestore: FirebaseFirestore,
     private val auth:FirebaseAuth
){
    suspend fun saveWaterData(totalDrunk: Int): Result<Unit> {
        val userId = auth.currentUser?.uid ?: return Result.failure(Exception("Kullanıcı oturumu açık değil"))
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        return try {
            val waterData = mapOf("totalDrunk" to totalDrunk)
            firestore.collection("users").document(userId)
                .collection("waterTracking").document(today)
                .set(waterData)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getWaterData(): Result<WaterState> {
        val userId = auth.currentUser?.uid ?: return Result.failure(Exception("Kullanıcı oturumu açık değil"))
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        return try {
            val document = firestore.collection("users").document(userId)
                .collection("waterTracking").document(today)
                .get()
                .await()

            val totalDrunk = document.getLong("totalDrunk")?.toInt() ?: 0
            Result.success(WaterState(totalDrunk = totalDrunk))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}