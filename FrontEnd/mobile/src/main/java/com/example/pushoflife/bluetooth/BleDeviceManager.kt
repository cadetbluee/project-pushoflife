package com.example.pushoflife.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import android.Manifest

class BleDeviceManager(private val context: Context) {

    private val TAG = "BleDeviceManager" // 로그 태그 정의

    private val deviceList = mutableListOf<BluetoothDevice>()
    private val addedDeviceAddresses = mutableSetOf<String>() // 추가된 기기 MAC 주소 추적용 Set

    @SuppressLint("MissingPermission")
    fun addDevice(device: BluetoothDevice) {
        // Android 12(API 31) 이상인 경우에만 권한 확인
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                Log.w(TAG, "BLUETOOTH_CONNECT permission 승인해주세여")
                return
            }
        }

        // 중복 추가 방지: Set을 사용하여 중복 MAC 주소 확인
        synchronized(deviceList) {
            if (addedDeviceAddresses.add(device.address)) { // Set에 새 주소 추가 성공 시만 실행
                deviceList.add(device)
                Log.d(TAG, "방금 기기가 추가 되었어용 : ${device.name ?: "Unknown"} (${device.address})")
            } else {
                Log.d(TAG, "기기 중복으로 추가하지 않음: ${device.address}")
            }
        }
    }

    fun clearDeviceList() {
        synchronized(deviceList) {
            deviceList.clear()
            addedDeviceAddresses.clear()
            Log.d(TAG, "기기 리스트 초기화 완료")
        }
    }


    // 스캔된 BLE 기기 리스트를 가져오는 메서드
    fun getDeviceList(): List<BluetoothDevice> = deviceList
}
