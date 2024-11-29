package com.example.pushoflife.di

import com.example.pushoflife.bluetooth.BleDeviceManager
import com.example.pushoflife.bluetooth.BleRepository
import com.example.pushoflife.bluetooth.BleManagerViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { BleManagerViewModel(get()) }
}

val repositoryModule = module {
    single {
        BleRepository()
    }
}