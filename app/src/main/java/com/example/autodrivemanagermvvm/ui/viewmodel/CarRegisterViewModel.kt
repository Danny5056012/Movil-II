package com.example.autodrivemanagermvvm.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.autodrivemanagermvvm.data.model.CarCreateDto
import com.example.autodrivemanagermvvm.data.repository.CarRepository
import com.example.autodrivemanagermvvm.ui.common.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CarRegisterViewModel(
    private val repository: CarRepository
) : ViewModel() {

    private val _make = MutableStateFlow("")
    private val _year = MutableStateFlow("")
    private val _model = MutableStateFlow("")
    private val _speed = MutableStateFlow("")
    private val _fuel = MutableStateFlow("")
    private val _selectedImageResId = MutableStateFlow<Int?>(null)

    val make: StateFlow<String> = _make.asStateFlow()
    val year: StateFlow<String> = _year.asStateFlow()
    val model: StateFlow<String> = _model.asStateFlow()
    val speed: StateFlow<String> = _speed.asStateFlow()
    val fuel: StateFlow<String> = _fuel.asStateFlow()

    val selectedImageResId: StateFlow<Int?> = _selectedImageResId.asStateFlow()

    val isSaveEnabled: StateFlow<Boolean> = combine(
        combine(_make, _year, _model, _speed, _fuel) { make, year, model, speed, fuel ->
            Quint(make, year, model, speed, fuel)
        },
        _selectedImageResId
    ) { q, imageResId ->
        val (make, year, model, speed, fuel) = q

        val yearInt = year.toIntOrNull()
        val speedInt = speed.toIntOrNull()
        val fuelInt = fuel.toIntOrNull()

        val textOk = make.isNotBlank() && model.isNotBlank() &&
            year.isNotBlank() && speed.isNotBlank() && fuel.isNotBlank()

        val yearOk = yearInt != null && yearInt in 1900..2026
        val speedOk = speedInt != null && speedInt > 0
        val fuelOk = fuelInt != null && fuelInt > 0
        val imageOk = imageResId != null

        textOk && yearOk && speedOk && fuelOk && imageOk
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    private data class Quint(
        val make: String,
        val year: String,
        val model: String,
        val speed: String,
        val fuel: String
    )

    private val _saveUiState = MutableStateFlow<UiState<Unit>>(UiState.Success(Unit))
    val saveUiState: StateFlow<UiState<Unit>> = _saveUiState.asStateFlow()

    fun updateMake(value: String) {
        _make.value = value
    }

    fun updateYear(value: String) {
        _year.value = value
    }

    fun updateModel(value: String) {
        _model.value = value
    }

    fun updateSpeed(value: String) {
        _speed.value = value
    }

    fun updateFuel(value: String) {
        _fuel.value = value
    }

    fun selectImage(resId: Int) {
        _selectedImageResId.value = resId
    }

    fun clearImageSelection() {
        _selectedImageResId.value = null
    }

    fun saveCar() {
        if (saveUiState.value is UiState.Loading) return
        val imageResId = _selectedImageResId.value ?: return

        val yearInt = _year.value.toIntOrNull() ?: return
        val speedInt = _speed.value.toIntOrNull() ?: return
        val fuelInt = _fuel.value.toIntOrNull() ?: return
        val make = _make.value.trim()
        val model = _model.value.trim()

        if (make.isBlank() || model.isBlank()) return
        if (yearInt !in 1900..2026) return
        if (speedInt <= 0 || fuelInt <= 0) return

        _saveUiState.value = UiState.Loading
        viewModelScope.launch {
            try {
                val created = repository.createCar(
                    create = CarCreateDto(
                        make = make,
                        year = yearInt,
                        model = model,
                        speed = speedInt,
                        fuel = fuelInt
                    ),
                    imageResId = imageResId
                )
                if (created > 0) _saveUiState.value = UiState.Success(Unit) else _saveUiState.value =
                    UiState.Error("No se pudo registrar el vehículo")
            } catch (e: Exception) {
                _saveUiState.value = UiState.Error(e.message ?: "Error al guardar")
            }
        }
    }
}

