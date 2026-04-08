package com.example.autodrivemanagermvvm.data.repository

import com.example.autodrivemanagermvvm.R
import com.example.autodrivemanagermvvm.data.api.CarApiService
import com.example.autodrivemanagermvvm.data.local.CarImageLocalStore
import com.example.autodrivemanagermvvm.data.local.TokenStorage
import com.example.autodrivemanagermvvm.data.model.AuthRequest
import com.example.autodrivemanagermvvm.data.model.AuthResponse
import com.example.autodrivemanagermvvm.data.model.CarCreateDto
import com.example.autodrivemanagermvvm.data.model.CarDto
import com.example.autodrivemanagermvvm.data.model.CarUiModel
import com.example.autodrivemanagermvvm.data.model.UserRegisterRequest
import com.example.autodrivemanagermvvm.data.network.ApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CarRepository(
    private val tokenStorage: TokenStorage = TokenStorage(),
    private val carImageLocalStore: CarImageLocalStore = CarImageLocalStore()
) {
    private val api: CarApiService = ApiClient.create(CarApiService::class.java)

    suspend fun login(email: String, password: String): String? = withContext(Dispatchers.IO) {
        val response: AuthResponse = api.login(AuthRequest(email, password))
        val token = response.extractToken()
        if (!token.isNullOrBlank()) {
            tokenStorage.setToken(token)
        }
        token
    }

    suspend fun registerUser(
        firstName: String,
        lastName: String,
        email: String,
        password: String
    ) = withContext(Dispatchers.IO) {
        api.register(
            UserRegisterRequest(
                first_name = firstName.trim(),
                last_name = lastName.trim(),
                email = email.trim(),
                password = password
            )
        )
    }

    suspend fun getCars(): List<CarUiModel> = withContext(Dispatchers.IO) {
        val cars: List<CarDto> = api.getCars()
        cars.map { it.toUi() }
    }

    suspend fun getCarDetail(id: Int): CarUiModel = withContext(Dispatchers.IO) {
        val car = api.getCarDetail(id)
        car.toUi()
    }

    suspend fun createCar(create: CarCreateDto, imageResId: Int): Int = withContext(Dispatchers.IO) {
        val created = api.createCar(create)
        carImageLocalStore.setCarImage(created.id, imageResId)
        created.id
    }

    suspend fun updateCar(id: Int, create: CarCreateDto, imageResId: Int?): Int =
        withContext(Dispatchers.IO) {
            val updated = api.updateCar(id, create)
            if (imageResId != null) {
                carImageLocalStore.setCarImage(updated.id, imageResId)
            }
            updated.id
        }

    suspend fun deleteCar(id: Int) = withContext(Dispatchers.IO) {
        api.deleteCar(id)
        // Nota: el borrado de la asociacion local se haria aqui si lo implementas.
    }

    private fun CarDto.toUi(): CarUiModel {
        val imageResId = carImageLocalStore.getCarImageResId(this.id, R.drawable.ic_car_1)
        return CarUiModel(
            id = this.id,
            make = this.make,
            year = this.year,
            model = this.model,
            speed = this.speed,
            fuel = this.fuel,
            imageResId = imageResId
        )
    }
}

