package com.example.autodrivemanagermvvm.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.autodrivemanagermvvm.ui.common.UiState
import com.example.autodrivemanagermvvm.data.repository.CarRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val repository: CarRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<UiState<Unit>>(UiState.Success(Unit))
    val uiState: StateFlow<UiState<Unit>> = _uiState.asStateFlow()

    fun login(email: String, password: String) {
        _uiState.value = UiState.Loading
        viewModelScope.launch {
            try {
                val token = repository.login(email.trim(), password)
                if (token.isNullOrBlank()) {
                    _uiState.value = UiState.Error("Credenciales inválidas")
                } else {
                    _uiState.value = UiState.Success(Unit)
                }
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Error al iniciar sesión")
            }
        }
    }
}

