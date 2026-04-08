package com.example.autodrivemanagermvvm.data.local

import android.content.Context
import android.content.SharedPreferences
import com.example.autodrivemanagermvvm.AppContext
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class CarImageLocalStore(
    context: Context = AppContext.appContext,
    private val prefs: SharedPreferences = context.getSharedPreferences("car_images_prefs", Context.MODE_PRIVATE)
) {
    private val gson = Gson()
    private val mapType = object : TypeToken<Map<Int, Int>>() {}.type
    private val key = "car_image_map"

    fun setCarImage(carId: Int, imageResId: Int) {
        val current = getMapInternal().toMutableMap()
        current[carId] = imageResId
        prefs.edit().putString(key, gson.toJson(current)).apply()
    }

    fun getCarImageResId(carId: Int, defaultResId: Int): Int {
        return getMapInternal()[carId] ?: defaultResId
    }

    fun clearAll() {
        prefs.edit().clear().apply()
    }

    private fun getMapInternal(): Map<Int, Int> {
        val raw = prefs.getString(key, null) ?: return emptyMap()
        return try {
            gson.fromJson(raw, mapType)
        } catch (_: Exception) {
            emptyMap()
        }
    }
}

