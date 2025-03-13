package com.example.kaloritakip.data.repository

import android.util.Log
import com.example.kaloritakip.data.model.FoodItem
import com.example.kaloritakip.data.model.UserInfo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserRepository @Inject constructor(private val auth:FirebaseAuth,
                     private val firestore: FirebaseFirestore) {

    suspend fun saveUserInfo(userInfo: UserInfo): Result<Unit> {
        return try {
            val userId = auth.currentUser?.uid
            userId?.let {
                firestore.collection("users")
                    .document(it)
                    .set(userInfo)
                    .await()
                Result.success(Unit)
            } ?: Result.failure(Exception("Kullanıcı oturumu yok"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserInfo(): Result<UserInfo> {
        return try {
            val userId = FirebaseAuth.getInstance().currentUser?.uid
            userId?.let {
                val document = FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(it)
                    .get()
                    .await()
                val userInfo = document.toObject(UserInfo::class.java)
                Result.success(userInfo ?: UserInfo())
            } ?: Result.failure(Exception("Kullanıcı oturumu yok"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addMeal(userId: String, meal: FoodItem) {
        try {
            val mealData = hashMapOf(
                "mealId" to meal.mealId,
                "mealType" to meal.mealType,
                "foodName" to meal.food_name,
                "calories" to meal.nf_calories,
                "protein" to meal.nf_protein,
                "carbs" to meal.nf_total_carbohydrate,
                "fat" to meal.nf_total_fat,
                "serving_weight_grams" to meal.serving_weight_grams,
                "serving_qty" to meal.serving_qty,
                "serving_unit" to meal.serving_unit
            )

            FirebaseFirestore.getInstance()
                .collection("users")
                .document(userId)
                .collection("meals")
                .document(meal.mealId)
                .set(mealData)
        } catch (e: Exception) {
            Log.e("Firestore", "Öğün kaydedilemedi: ${e.message}")
        }
    }

    suspend fun getMeals(userId: String): List<FoodItem> {
        return try {
            val snapshot = FirebaseFirestore.getInstance()
                .collection("users")
                .document(userId)
                .collection("meals")
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                FoodItem(
                    mealId = doc.getString("mealId") ?: "",
                    mealType = doc.getString("mealType") ?: "",
                    food_name = doc.getString("foodName") ?: "",
                    nf_calories = doc.getDouble("calories") ?: 0.0,
                    nf_protein = doc.getDouble("protein") ?: 0.0,
                    nf_total_carbohydrate = doc.getDouble("carbs") ?: 0.0,
                    nf_total_fat = doc.getDouble("fat") ?: 0.0,
                    serving_weight_grams = doc.getDouble("serving_weight_grams") ?: 0.0,
                    serving_qty = doc.getDouble("serving_qty")?.toInt() ?: 1,
                    serving_unit = doc.getString("serving_unit") ?: ""
                )
            }
        } catch (e: Exception) {
            Log.e("Firestore", "Öğünleri alırken hata: ${e.message}")
            emptyList()
        }
    }

    suspend fun removeMeal(userId: String, mealId: String) {
        try {
            val mealsRef = FirebaseFirestore.getInstance()
                .collection("users")
                .document(userId)
                .collection("meals")

            val querySnapshot = mealsRef.whereEqualTo("mealId", mealId).get().await()

            for (document in querySnapshot.documents) {
                document.reference.delete()
            }
        } catch (e: Exception) {
            Log.e("Firestore", "Öğün silinemedi: ${e.message}")
        }
    }
}