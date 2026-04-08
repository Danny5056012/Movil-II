package com.example.autodrivemanagermvvm.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.autodrivemanagermvvm.data.repository.CarRepository

class ViewModelFactory(
    private val repository: CarRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> LoginViewModel(repository) as T
            modelClass.isAssignableFrom(RegisterViewModel::class.java) -> RegisterViewModel(repository) as T
            modelClass.isAssignableFrom(CarListViewModel::class.java) -> CarListViewModel(repository) as T
            modelClass.isAssignableFrom(CarRegisterViewModel::class.java) -> CarRegisterViewModel(repository) as T
            modelClass.isAssignableFrom(CarDetailViewModel::class.java) -> CarDetailViewModel(repository) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}

