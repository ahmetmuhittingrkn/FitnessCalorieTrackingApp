package com.example.kaloritakip.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kaloritakip.data.model.ExerciseLog
import com.example.kaloritakip.data.model.ExerciseResponse
import com.example.kaloritakip.data.repository.ExerciseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class ExerciseViewModel @Inject constructor(
    private val repository: ExerciseRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ExerciseUiState>(ExerciseUiState.Idle)
    val uiState: StateFlow<ExerciseUiState> = _uiState.asStateFlow()

    private val _logUiState = MutableStateFlow<ExerciseLogUiState>(ExerciseLogUiState.Idle)
    val logUiState: StateFlow<ExerciseLogUiState> = _logUiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("Tümü")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private var searchJob: Job? = null


    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        performSearch()
    }


    fun selectCategory(category: String) {
        _selectedCategory.value = category
        performSearch()
    }

    fun addExercise(exerciseLog: ExerciseLog) {
        viewModelScope.launch {
            val todayDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

            repository.addExerciseLog(
                exerciseLog.copy(date = todayDate)
            ).onSuccess {
                getTodayExercises()
            }
        }
    }

    private fun performSearch() {
        val query = _searchQuery.value
        val category = _selectedCategory.value

        if (query.isBlank() && category == "Tümü") {
            _uiState.value = ExerciseUiState.Idle
            return
        }
        val bodyPart = mapCategoryToBodyPart(category)

        if (category == "Tümü") {
            searchExercises(query)
        } else {
            searchExercisesWithFilters(query, bodyPart)
        }
    }

    private fun searchExercises(query: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            if (query.isBlank()) {
                _uiState.value = ExerciseUiState.Idle
                return@launch
            }

            delay(500)

            _uiState.value = ExerciseUiState.Loading
            val encodedQuery = URLEncoder.encode(query, "UTF-8")

            repository.searchExercises(encodedQuery)
                .onSuccess { exercises ->
                    _uiState.value = ExerciseUiState.Success(exercises)
                }
                .onFailure { error ->
                    _uiState.value = ExerciseUiState.Error(error.message ?: "Egzersizler yüklenemedi.")
                }
        }
    }

    private fun searchExercisesWithFilters(query: String, bodyPart: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            _uiState.value = ExerciseUiState.Loading
            delay(500)

            if (bodyPart.isNotBlank()) {
                repository.searchExercisesByBodyPart(bodyPart)
                    .onSuccess { exercises ->
                        val filteredExercises = if (query.isNotBlank()) {
                            exercises.filter { exercise ->
                                exercise.name.contains(query, ignoreCase = true) ||
                                        exercise.equipment.contains(query, ignoreCase = true) ||
                                        exercise.target.contains(query, ignoreCase = true) ||
                                        exercise.secondaryMuscles.any { it.contains(query, ignoreCase = true) }
                            }
                        } else {
                            exercises
                        }
                        _uiState.value = ExerciseUiState.Success(filteredExercises)
                    }
                    .onFailure { error ->
                        _uiState.value = ExerciseUiState.Error(error.message ?: "Egzersizler yüklenemedi.")
                    }
            } else {
                if (query.isNotBlank()) {
                    searchExercises(query)
                } else {
                    _uiState.value = ExerciseUiState.Idle
                }
            }
        }
    }

    fun getTodayExercises() {
        viewModelScope.launch {
            _logUiState.value = ExerciseLogUiState.Loading
            val result = repository.getUserExercises()
            if (result.isSuccess) {
                val allExercises = result.getOrNull() ?: emptyList()

                val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                val todayExercises = allExercises.filter { it.date == today }

                _logUiState.value = ExerciseLogUiState.Success(todayExercises)
            } else {
                _logUiState.value = ExerciseLogUiState.Error(result.exceptionOrNull()?.message ?: "Egzersizler yüklenemedi.")
            }
        }
    }

    fun deleteExercise(exerciseId: String) {
        viewModelScope.launch {
            repository.deleteExerciseLog(exerciseId)
                .onSuccess {
                    getTodayExercises()
                }
                .onFailure { error ->
                    _logUiState.value = ExerciseLogUiState.Error(error.message ?: "Egzersiz silinemedi.")
                }
        }
    }

    private fun mapCategoryToBodyPart(category: String): String {
        return when (category) {
            "Kol" -> "upper arms"
            "Göğüs" -> "chest"
            "Sırt" -> "back"
            "Bacak" -> "upper legs"
            "Karın" -> "waist"
            "Omuz" -> "shoulders"
            else -> ""
        }
    }
}


sealed class ExerciseUiState {
    object Idle : ExerciseUiState()
    object Loading : ExerciseUiState()
    data class Success(val exercises: List<ExerciseResponse>) : ExerciseUiState()
    data class Error(val message: String) : ExerciseUiState()
}


sealed class ExerciseLogUiState {
    object Idle : ExerciseLogUiState()
    object Loading : ExerciseLogUiState()
    data class Success(val exerciseLogs: List<ExerciseLog>) : ExerciseLogUiState()
    data class Error(val message: String) : ExerciseLogUiState()
}
