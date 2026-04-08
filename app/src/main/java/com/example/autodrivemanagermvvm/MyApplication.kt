package com.example.autodrivemanagermvvm

import android.app.Application

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AppContext.init(this)
    }
}

