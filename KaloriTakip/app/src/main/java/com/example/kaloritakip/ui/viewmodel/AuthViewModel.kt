package com.example.kaloritakip.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kaloritakip.data.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(private val authRepository: AuthRepository) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> get() = _authState

    private val _sessionState = MutableStateFlow<SessionState>(SessionState.Idle)
    val sessionState: StateFlow<SessionState> get() = _sessionState

    private val _logOutState = MutableStateFlow<Result<Unit>?>(null)
    val logOutState : StateFlow<Result<Unit>?> get() = _logOutState

    private var isLogin = false

    fun login(email: String, password: String) {
        viewModelScope.launch {
            isLogin = true
            _authState.value = AuthState.Loading
            val result = authRepository.login(email, password)
            if (result.isSuccess) {
                _authState.value = AuthState.Success("Giriş Başarılı")
                _sessionState.value = SessionState.Authenticated
            } else {
                _authState.value = AuthState.Error(result.exceptionOrNull()?.message ?: "Bilinmeyen hata")
            }
        }
    }

    fun register(email: String, password: String) {
        viewModelScope.launch {
            isLogin = false
            _authState.value = AuthState.Loading
            val result = authRepository.register(email, password)
            if (result.isSuccess) {
                _authState.value = AuthState.Success("Kayıt Başarılı")
                _sessionState.value = SessionState.Unauthenticated
            } else {
                _authState.value = AuthState.Error(result.exceptionOrNull()?.message ?: "Bilinmeyen hata")
            }
        }
    }


    fun logOut() {
        viewModelScope.launch {
            val result = authRepository.logOut()
            _logOutState.value = result
            if (result.isSuccess) {
                _sessionState.value = SessionState.Unauthenticated
                Log.d("AuthViewModel", "Logged out, session state: ${_sessionState.value}")
            }
        }
    }

    fun setErrorMessage(message: String) {
        _authState.value = AuthState.Error(message)
    }

    fun resetLogOutState() {
        _logOutState.value = null
    }

    fun resetAuthState() {
        _authState.value = AuthState.Idle
    }

    fun checkUserSession() {
        val currentUser = authRepository.getCurrentUser()
        if (currentUser != null) {
            _sessionState.value = SessionState.Authenticated
        } else {
            _sessionState.value = SessionState.Unauthenticated
        }
    }
}

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val message: String) : AuthState()
    data class Error(val message: String) : AuthState()
}

sealed class SessionState {
    object Idle : SessionState()
    object Authenticated : SessionState()
    object Unauthenticated : SessionState()
}
