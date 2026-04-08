package com.example.autodrivemanagermvvm.data.network

import com.example.autodrivemanagermvvm.data.local.CarImageLocalStore
import com.example.autodrivemanagermvvm.data.local.TokenStorage
import com.example.autodrivemanagermvvm.data.session.SessionEvent
import com.example.autodrivemanagermvvm.data.session.SessionEventBus
import java.io.IOException
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val tokenStorage: TokenStorage,
    private val carImageLocalStore: CarImageLocalStore
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val token = tokenStorage.getToken()

        val requestWithAuth = if (!token.isNullOrBlank()) {
            originalRequest.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        } else {
            originalRequest
        }

        val response = chain.proceed(requestWithAuth)
        if (response.code != 401) return response

        // Manejo de caducidad: limpiamos preferencias y notificamos a la UI.
        tokenStorage.clearAll()
        carImageLocalStore.clearAll()
        SessionEventBus.emit(SessionEvent.Expired)

        return response
    }
}

