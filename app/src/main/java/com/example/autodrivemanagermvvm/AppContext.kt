package com.example.autodrivemanagermvvm

import android.content.Context

object AppContext {
    lateinit var appContext: Context
        private set

    fun init(context: Context) {
        if (!::appContext.isInitialized) {
            appContext = context.applicationContext
        }
    }
}

