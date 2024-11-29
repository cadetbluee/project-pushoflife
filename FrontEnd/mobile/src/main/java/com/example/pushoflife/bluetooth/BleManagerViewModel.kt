package com.example.pushoflife.bluetooth

import android.bluetooth.BluetoothDevice
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pushoflife.utils.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.nio.ByteBuffer

class BleManagerViewModel(private val bleRepository: BleRepository) : ViewModel() {

    val _isScanning: LiveData<Event<Boolean>> = bleRepository.isScanning
    val listUpdate: MutableLiveData<Event<List<BluetoothDevice>?>> = bleRepository.listUpdate

    private var isScanning = false // 스캔 중인지 여부를 관리하는 플래그

    companion object {
        private const val TAG = "BleManagerViewModel" // TAG 정의
    }

    fun onWatchAlertReceived(message: ByteArray) {
        if (!isScanning) { // 스캔 중이 아닐 때만 실행
            Log.d(TAG, "스캔 시작할게유")
            startEmergencyScan(message)
        } else {
            Log.d(TAG, "이미 스캔 중입니다.")
        }
    }

    private fun startEmergencyScan(message: ByteArray) {
        isScanning = true // 스캔 시작 시 플래그 설정
        viewModelScope.launch {
            Log.d(TAG, "스 캔 시 작")
            bleRepository.startScan { // 콜백 전달
                onScanComplete(message) // 스캔 완료 시 onScanComplete 호출
                isScanning = false // 스캔 완료 후 플래그 해제
            }
        }
    }

    fun onScanComplete(message: ByteArray) {
        Log.d(TAG, "BLE 스캔 끝 ! 기기들한테 메세지 보내러 간다.")
        Log.d(TAG, message.toString())

        // ByteArray에서 latitude와 longitude를 추출
        val byteBuffer = ByteBuffer.wrap(message)
        val latitude = byteBuffer.double // 앞의 8바이트는 latitude
        val longitude = byteBuffer.double // 뒤의 8바이트는 longitude

        viewModelScope.launch(Dispatchers.IO) {
            Log.d(TAG, "연결하기 전에 4초 기다림")
            delay(3000) // 10-second delay
            bleRepository.sendLocationToAllDevices(latitude, longitude)
        }
    }
}
