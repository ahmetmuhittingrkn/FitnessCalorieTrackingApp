package com.example.kaloritakip.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kaloritakip.data.model.WaterState
import com.example.kaloritakip.data.repository.WaterRepository
import dagger.hilt.android.lifecycle.HiltViewModel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WaterViewModel @Inject constructor(private val waterRepository: WaterRepository) : ViewModel(){

    private val _statusState = MutableStateFlow<WaterUiState>(WaterUiState.Idle)
    val statusState : StateFlow<WaterUiState> get() = _statusState

    private val _waterState = MutableStateFlow(WaterState())
    val waterState: StateFlow<WaterState> = _waterState

    fun addWater(amount:Int) {
        viewModelScope.launch {
            _statusState.value = WaterUiState.Loading
            val newTotal = _waterState.value.totalDrunk + amount
            val result = waterRepository.saveWaterData(newTotal)

            if (result.isSuccess) {
                _waterState.value = _waterState.value.copy(totalDrunk = newTotal)
                _statusState.value = WaterUiState.Success("Su başarıyla eklendi!")
            } else {
                _statusState.value = WaterUiState.Error(result.exceptionOrNull()?.message ?: "Bilinmeyen hata")
            }
        }
    }

    fun getWater() {
        viewModelScope.launch {
            viewModelScope.launch {
                _statusState.value = WaterUiState.Loading
                val result = waterRepository.getWaterData()

                if (result.isSuccess) {
                    _waterState.value = result.getOrNull() ?: WaterState()
                    _statusState.value = WaterUiState.Success("Veriler yüklendi")
                } else {
                    _statusState.value = WaterUiState.Error(result.exceptionOrNull()?.message ?: "Veri yüklenemedi")
                }
            }
        }
    }
}

sealed class WaterUiState{
    object Idle : WaterUiState()
    object Loading : WaterUiState()
    data class Success(val message:String) : WaterUiState()
    data class Error(val message: String) : WaterUiState()
}