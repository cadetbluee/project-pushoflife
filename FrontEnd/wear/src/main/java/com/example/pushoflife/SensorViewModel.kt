package com.example.pushoflife

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SensorViewModel(context: Context) : ViewModel(), SensorEventListener {

    // 가속도계 데이터
    private val _accelerometerData = MutableStateFlow("Accelerometer: No data")
    val accelerometerData: StateFlow<String> = _accelerometerData

    // 자이로스코프 데이터
    private val _gyroscopeData = MutableStateFlow("Gyroscope: No data")
    val gyroscopeData: StateFlow<String> = _gyroscopeData

    // 심박수 데이터 (마지막 심박수 유지)
    private var lastHeartRate: String = "No data"
    private val _heartRateData = MutableStateFlow("Heart Rate: $lastHeartRate")
    val heartRateData: StateFlow<String> = _heartRateData

    private val sensorManager: SensorManager =
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometer: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val gyroscope: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
    private val heartRateSensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE)

    init {
        // 센서 리스너 등록
        accelerometer?.also {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
        gyroscope?.also {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
        heartRateSensor?.also {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onSensorChanged(event: SensorEvent) {
        viewModelScope.launch {
            when (event.sensor.type) {
                Sensor.TYPE_ACCELEROMETER -> {
                    val x = String.format("%.2f", event.values[0])
                    val y = String.format("%.2f", event.values[1])
                    val z = String.format("%.2f", event.values[2])
                    _accelerometerData.value = "Accelerometer: x=$x, y=$y, z=$z"
                }
                Sensor.TYPE_GYROSCOPE -> {
                    val x = String.format("%.2f", event.values[0])
                    val y = String.format("%.2f", event.values[1])
                    val z = String.format("%.2f", event.values[2])
                    _gyroscopeData.value = "Gyroscope: x=$x, y=$y, z=$z"
                }
                Sensor.TYPE_HEART_RATE -> {
                    val heartRate = String.format("%.2f", event.values[0])
                    lastHeartRate = heartRate  // 마지막 심박수 업데이트
                    _heartRateData.value = "Heart Rate: $lastHeartRate bpm"
                    Log.d("SensorData", "Heart Rate: $heartRate bpm")  // 로그에 심박수 출력
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // 정확도 변경 처리 필요 시 구현
    }

    override fun onCleared() {
        super.onCleared()
        sensorManager.unregisterListener(this)
    }
}

