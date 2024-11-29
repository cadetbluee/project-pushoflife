package com.example.pushoflife.utils

import android.Manifest
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.content.ContextCompat

private const val TAG = "WearDeviceChecker"

fun isWearDeviceConnected(context: Context): Boolean {
    // Check Bluetooth permission
    if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
//        Log.d(TAG, "BLUETOOTH_CONNECT permission is missing")
        return false
    }

    val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager
    val bluetoothAdapter = bluetoothManager?.adapter

    if (bluetoothAdapter != null && bluetoothAdapter.isEnabled) {
//        Log.d(TAG, "Bluetooth adapter is enabled")

        val pairedDevices = bluetoothAdapter.bondedDevices
//        Log.d(TAG, "Number of paired devices: ${pairedDevices.size}")

        for (device in pairedDevices) {
//            Log.d(TAG, "Paired device name: ${device.name}")
            if (device.name?.contains("Watch", ignoreCase = true) == true) {
//                Log.d(TAG, "Wear OS device found: ${device.name}")
                return true
            }
        }
//        Log.d(TAG, "No Wear OS device found among paired devices")
    } else {
//        Log.d(TAG, "Bluetooth adapter is unavailable")
    }

    val isWearDevice = context.packageManager.hasSystemFeature(PackageManager.FEATURE_WATCH)
//    Log.d(TAG, "Wear OS device status via system feature: $isWearDevice")

    return isWearDevice
}
