package com.example.autodrivemanagermvvm.data.api

import com.example.autodrivemanagermvvm.data.model.AuthRequest
import com.example.autodrivemanagermvvm.data.model.AuthResponse
import com.example.autodrivemanagermvvm.data.model.CarCreateDto
import com.example.autodrivemanagermvvm.data.model.CarDto
import com.example.autodrivemanagermvvm.data.model.UserRegisterRequest
import com.example.autodrivemanagermvvm.data.model.UserRegisterResponse
import com.example.autodrivemanagermvvm.data.network.ApiConfig
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface CarApiService {
    @POST(ApiConfig.LOGIN_ENDPOINT)
    suspend fun login(@Body request: AuthRequest): AuthResponse

    @POST(ApiConfig.USERS_ENDPOINT)
    suspend fun register(@Body request: UserRegisterRequest): UserRegisterResponse

    @GET(ApiConfig.CARS_ENDPOINT)
    suspend fun getCars(): List<CarDto>

    @GET("${ApiConfig.CARS_ENDPOINT}{id}")
    suspend fun getCarDetail(@Path("id") id: Int): CarDto

    @POST(ApiConfig.CARS_ENDPOINT)
    suspend fun createCar(@Body request: CarCreateDto): CarDto

    @PATCH("${ApiConfig.CARS_ENDPOINT}{id}")
    suspend fun updateCar(
        @Path("id") id: Int,
        @Body request: CarCreateDto
    ): CarDto

    @DELETE("${ApiConfig.CARS_ENDPOINT}{id}")
    suspend fun deleteCar(@Path("id") id: Int)
}

