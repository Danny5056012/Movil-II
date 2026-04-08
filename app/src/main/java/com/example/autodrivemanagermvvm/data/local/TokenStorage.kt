package com.example.autodrivemanagermvvm.data.local

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.example.autodrivemanagermvvm.AppContext

class TokenStorage(context: Context = AppContext.appContext) {
    private val prefsName = "secure_session_prefs"
    private val tokenKey = "access_token"

    private val masterKey: MasterKey by lazy {
        MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build()
    }

    private val prefs by lazy {
        EncryptedSharedPreferences.create(
            context,
            prefsName,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    fun getToken(): String? = prefs.getString(tokenKey, null)

    fun setToken(token: String) {
        prefs.edit().putString(tokenKey, token).apply()
    }

    fun clearAll() {
        prefs.edit().clear().apply()
    }
}

