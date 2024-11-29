package com.example.pushoflife.bluetooth

import android.Manifest
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class BleDeviceService : Service() {

    // ViewModel을 Koin을 통해 inject()로 주입받기
    private val viewModel: BleManagerViewModel by inject()
    private lateinit var bleDeviceManager: BleDeviceManager  // BleDeviceManager 인스턴스 생성

    override fun onCreate() {
        super.onCreate()
        bleDeviceManager = BleDeviceManager(this)

        // Android 버전에 따른 권한 확인 및 광고 시작
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!hasPermissions(PERMISSIONS)) {
                requestPermissions()
            } else {
                Log.d("song", "SDK 버전 높지만 권한 허용 했구 광고 시작")
                startBleAdvertiseService()
            }
        } else {
            Log.d("song", "SDK 버전 낮아서 바로 광고 시작")
            startBleAdvertiseService()
        }

        // 스캔 결과를 관찰하여 BleDeviceManager에 저장
        initObserver()
    }

    private fun requestPermissions() {
        CoroutineScope(Dispatchers.Main).launch {
            val permissions = PERMISSIONS.filter {
                ContextCompat.checkSelfPermission(this@BleDeviceService, it) != PackageManager.PERMISSION_GRANTED
            }
            if (permissions.isEmpty()) {
                Log.d("song", "권한이 필요한데 다 허용해주셨네용 광고 합니다요")
                startBleAdvertiseService()
            }
//            else {
//                // 권한 요청 로직 작성 필요 (Foreground Service에서는 ActivityResultContracts 사용이 제한적)
//                Toast.makeText(this@BleDeviceService, "권한을 허용해주세요 !!", Toast.LENGTH_SHORT).show()
//                val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
//                intent.data = Uri.parse("package:$packageName")
//                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//                startActivity(intent)
//            }
        }
    }

    // 권한이 있는 경우 Foreground Service를 통해 BLE 광고 시작
    private fun startBleAdvertiseService() {
        Log.d("song", "진짜로 광고하러 서비스 감")
        val serviceIntent = Intent(this, BleAdvertiseService::class.java)
        ContextCompat.startForegroundService(this, serviceIntent)
    }

    private fun initObserver() {
        viewModel.listUpdate.observeForever {
            it.getContentIfNotHandled()?.let { scanResults ->
                scanResults.forEach { device ->
                    bleDeviceManager.addDevice(device)  // 스캔된 기기를 BleDeviceManager에 추가
                }
            }
        }
    }

    private fun hasPermissions(permissions: Array<String>): Boolean {
        return permissions.all { permission ->
            ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    companion object {
        // 필요한 BLE 관련 권한
        val PERMISSIONS = arrayOf(
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_ADVERTISE
        )
    }
}
