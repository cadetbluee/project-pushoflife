package com.example.pushoflife.sensor.utils

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log

object HeartRateSensor : SensorEventListener {
    private var sensorManager: SensorManager? = null
    private var heartRateSensor: Sensor? = null
    private var currentHeartRate: Float = 0.0f  // 가장 최근의 심박수 값 저장

    // HeartRateSensor 초기화 및 리스너 등록
    fun initialize(context: Context) {
        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        heartRateSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_HEART_RATE)
        heartRateSensor?.let {
            sensorManager?.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    // 센서 데이터가 업데이트될 때마다 호출
    override fun onSensorChanged(event: SensorEvent) {
        currentHeartRate = event.values[0]
        Log.d("HeartRateSensor", "Current Heart Rate: $currentHeartRate")
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    // 리스너 해제 메서드
    fun stop() {
        sensorManager?.unregisterListener(this)
    }

    // 외부에서 현재 심박수 값을 얻을 수 있는 메서드
    fun getCurrentHeartRate(): Float = currentHeartRate
}
