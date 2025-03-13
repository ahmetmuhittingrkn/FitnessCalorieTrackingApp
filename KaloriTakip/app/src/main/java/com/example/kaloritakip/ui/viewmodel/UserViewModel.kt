package com.example.kaloritakip.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kaloritakip.data.model.FoodItem
import com.example.kaloritakip.data.model.UserInfo
import com.example.kaloritakip.data.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(private val userRepository: UserRepository,
                                        private val firebaseFirestore: FirebaseFirestore): ViewModel() {

    private val _userInfoState = MutableStateFlow<UserInfo?>(null)
    val userInfoState: StateFlow<UserInfo?> get() = _userInfoState

    private val _statusState = MutableStateFlow<UserInfoState>(UserInfoState.Idle)
    val statusState: StateFlow<UserInfoState> get() = _statusState

    private val _bmrState = MutableStateFlow(0.0)
    val bmrState: StateFlow<Double> get() = _bmrState

    private val _meals = MutableStateFlow<List<FoodItem>>(emptyList())
    val meals: StateFlow<List<FoodItem>> get() = _meals

    private val _totalCalories = MutableStateFlow(0.0)
    val totalCalories: StateFlow<Double> = _totalCalories

    private val _totalCarbs = MutableStateFlow(0.0)
    val totalCarbs: StateFlow<Double> = _totalCarbs

    private val _totalProtein = MutableStateFlow(0.0)
    val totalProtein: StateFlow<Double> = _totalProtein

    private val _totalFat = MutableStateFlow(0.0)
    val totalFat: StateFlow<Double> = _totalFat

    init {
        getUserInfo()
        getMeals()
    }

    fun saveUserInfo(userInfo: UserInfo) {
        viewModelScope.launch {
            _statusState.value = UserInfoState.Loading
            val result = userRepository.saveUserInfo(userInfo)
            if (result.isSuccess) {
                _userInfoState.value = userInfo
                _statusState.value = UserInfoState.Success("Bilgiler kaydedildi")
            } else {
                _statusState.value =
                    UserInfoState.Error(result.exceptionOrNull()?.message ?: "Bilinmeyen hata")
            }
        }
    }

    fun getUserInfo() {
        viewModelScope.launch {
            _statusState.value = UserInfoState.Loading
            val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return@launch
            firebaseFirestore.collection("users").document(uid).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val user = document.toObject(UserInfo::class.java)
                        if (user != null) {
                            val calculatedBMR = calculateBMR(user)
                            val currentEmail = FirebaseAuth.getInstance().currentUser?.email ?: ""

                            val updatedUser = user.copy(
                                bmr = calculatedBMR,
                                email = if (user.email.isEmpty()) currentEmail else user.email
                            )
                            _userInfoState.value = updatedUser
                            _statusState.value = UserInfoState.Success("Veriler Yüklendi")
                        }
                    } else {
                        _userInfoState.value = null
                        _statusState.value = UserInfoState.Idle
                    }
                }
                .addOnFailureListener {
                    _userInfoState.value = null
                    _statusState.value = UserInfoState.Error(it.message ?: "Veri yüklenemedi")
                }
        }
    }

    fun resetUserInfoState() {
        _userInfoState.value = null
        _statusState.value = UserInfoState.Idle
        _meals.value = emptyList()
        _totalCalories.value = 0.0
        _totalCarbs.value = 0.0
        _totalProtein.value = 0.0
        _totalFat.value = 0.0
        _bmrState.value = 0.0
    }

    fun addMeal(meal: FoodItem) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        viewModelScope.launch {
            userRepository.addMeal(userId, meal)
        }
        getMeals()
    }

    fun getMeals() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        viewModelScope.launch {
            _meals.value = userRepository.getMeals(userId)
            calculateTotalNutrients()
        }
    }

    fun removeMeal(meal: FoodItem) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        viewModelScope.launch {
            userRepository.removeMeal(userId, meal.mealId)
            _meals.value = _meals.value.filter { it.mealId != meal.mealId }
        }
        getMeals()
    }

    fun calculateBMR(userInfo: UserInfo) : Double{
        val bmr = when (userInfo.gender.lowercase()) {
            "erkek" -> 66.5 + (13.75 * userInfo.weight) + (5.003 * userInfo.height) - (6.75 * userInfo.age)
            "kadın" -> 655.1 + (9.563 * userInfo.weight) + (1.850 * userInfo.height) - (4.676 * userInfo.age)
            else -> 0.0
        }

        val activityFactor = when (userInfo.activityLevel.lowercase()) {
            "düşük aktivite" -> 1.2
            "orta aktivite" -> 1.55
            "yüksek aktivite" -> 1.9
            else -> 1.0
        }

        val result = bmr * activityFactor
        _bmrState.value = result
        return result
    }

    fun calculateTotalNutrients() {
        val meals = _meals.value
        _totalCalories.value = meals.sumOf { it.nf_calories }
        _totalCarbs.value = meals.sumOf { it.nf_total_carbohydrate }
        _totalProtein.value = meals.sumOf { it.nf_protein }
        _totalFat.value = meals.sumOf { it.nf_total_fat }
    }


}

sealed class UserInfoState {
    object Idle : UserInfoState()
    object Loading : UserInfoState()
    data class Success(val message: String) : UserInfoState()
    data class Error(val message: String) : UserInfoState()
}