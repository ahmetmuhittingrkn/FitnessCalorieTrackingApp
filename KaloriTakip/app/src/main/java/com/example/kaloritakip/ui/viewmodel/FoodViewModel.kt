package com.example.kaloritakip.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kaloritakip.data.model.FoodItem
import com.example.kaloritakip.data.model.TurkishFoodItem
import com.example.kaloritakip.data.repository.FoodRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FoodViewModel @Inject constructor(private val foodRepository: FoodRepository) : ViewModel(){

    private val _foodState = MutableStateFlow<FoodUiState>(FoodUiState.Idle)
    val foodState: StateFlow<FoodUiState> get() = _foodState

    fun searchFood(query: String) {
        viewModelScope.launch {
            _foodState.value = FoodUiState.Loading
            try {
                val turkishFoods = foodRepository.searchFood(query)
                _foodState.value = FoodUiState.Success(turkishFoods)
            } catch (e: Exception) {
                _foodState.value = FoodUiState.Error(e.localizedMessage ?: "Bir hata oluştu")
            }
        }
    }
}

sealed class FoodUiState {
    object Idle : FoodUiState()
    object Loading : FoodUiState()
    data class Success(val foods: List<TurkishFoodItem>) : FoodUiState()
    data class Error(val message: String) : FoodUiState()
}
