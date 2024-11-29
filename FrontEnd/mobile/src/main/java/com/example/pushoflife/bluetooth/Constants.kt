package com.example.pushoflife.bluetooth


import android.Manifest

// used to identify adding bluetooth names
const val REQUEST_ENABLE_BT = 1
// used to request fine location permission
const val REQUEST_ALL_PERMISSION = 2
val PERMISSIONS = arrayOf(
    Manifest.permission.ACCESS_FINE_LOCATION
)

//사용자 BLE UUID Service/Rx/Tx
const val SERVICE_UUID = "AD721038-83DA-4D63-9C5A-2A1DE229BEDC"
const val CHARACTERISTIC_WRITE_UUID = "16ECF1F8-04B7-4CD9-AE7B-69EDF1229988"
const val CHARACTERISTIC_READ_UUID = "9DF2D7C9-4CF5-41AE-88C3-0DAE2E2A78AE"

//BluetoothGattDescriptor 고정
const val CLIENT_CHARACTERISTIC_CONFIG = "D551E809-5F8E-48B5-94DA-D37DCD0A3FBF"