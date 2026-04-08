package com.example.autodrivemanagermvvm.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.autodrivemanagermvvm.data.model.CarUiModel
import com.example.autodrivemanagermvvm.data.repository.CarRepository
import com.example.autodrivemanagermvvm.ui.common.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CarDetailViewModel(
    private val repository: CarRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<CarUiModel>>(UiState.Loading)
    val uiState: StateFlow<UiState<CarUiModel>> = _uiState.asStateFlow()

    private val _deleteState = MutableStateFlow<UiState<Unit>>(UiState.Success(Unit))
    val deleteState: StateFlow<UiState<Unit>> = _deleteState.asStateFlow()

    fun loadCarDetail(id: Int) {
        _uiState.value = UiState.Loading
        viewModelScope.launch {
            try {
                val car = repository.getCarDetail(id)
                _uiState.value = UiState.Success(car)
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Error al cargar el detalle")
            }
        }
    }

    fun deleteCar(id: Int) {
        _deleteState.value = UiState.Loading
        viewModelScope.launch {
            try {
                repository.deleteCar(id)
                _deleteState.value = UiState.Success(Unit)
            } catch (e: Exception) {
                _deleteState.value = UiState.Error(e.message ?: "Error al eliminar")
            }
        }
    }

    fun getSpeedInMph(speedKmh: Int): Double = speedKmh * 0.62
}

