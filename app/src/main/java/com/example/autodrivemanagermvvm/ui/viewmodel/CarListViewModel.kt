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

class CarListViewModel(
    private val repository: CarRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<List<CarUiModel>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<CarUiModel>>> = _uiState.asStateFlow()

    private val allCars = mutableListOf<CarUiModel>()
    private var searchQuery: String = ""

    init {
        refresh()
    }

    fun refresh() {
        _uiState.value = UiState.Loading
        viewModelScope.launch {
            try {
                val cars = repository.getCars()
                allCars.clear()
                allCars.addAll(cars)
                applyFilterAndEmit()
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Error al cargar vehículos")
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        searchQuery = query
        applyFilterAndEmit()
    }

    private fun applyFilterAndEmit() {
        if (allCars.isEmpty()) {
            _uiState.value = UiState.Success(emptyList())
            return
        }

        val q = searchQuery.trim().lowercase()
        val filtered = if (q.isEmpty()) {
            allCars.toList()
        } else {
            allCars.filter {
                it.make.lowercase().contains(q) ||
                    it.model.lowercase().contains(q) ||
                    it.year.toString().contains(q)
            }
        }
        _uiState.value = UiState.Success(filtered)
    }
}

