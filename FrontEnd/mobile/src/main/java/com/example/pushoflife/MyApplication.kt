package com.example.pushoflife

import android.app.Application
import android.content.Context
import android.util.Log
import com.example.pushoflife.di.repositoryModule
import com.example.pushoflife.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidFileProperties
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class MyApplication : Application() {
    init{
        instance = this
    }
    companion object {
        lateinit var instance: MyApplication
        fun applicationContext() : Context {
            return instance.applicationContext
        }
    }
override fun onCreate() {
    super.onCreate()
    Log.d("MyApplication", "Application context initialized: $applicationContext")

    startKoin {
        androidContext(this@MyApplication)
        modules(listOf(repositoryModule, viewModelModule))
    }
    Log.d("MyApplication", "Koin started successfully")
}

}
