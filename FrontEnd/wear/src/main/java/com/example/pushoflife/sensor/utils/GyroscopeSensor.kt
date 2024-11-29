package com.example.pushoflife.sensor.utils

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log

object GyroscopeSensor : SensorEventListener {
    private var sensorManager: SensorManager? = null
    private var gyroscopeSensor: Sensor? = null
    private var currentX: Float = 0.0f
    private var currentY: Float = 0.0f
    private var currentZ: Float = 0.0f

    // GyroscopeSensor 초기화 및 리스너 등록
    fun initialize(context: Context) {
        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        gyroscopeSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        gyroscopeSensor?.let {
            sensorManager?.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    // 센서 데이터가 업데이트될 때마다 호출
    override fun onSensorChanged(event: SensorEvent) {
        currentX = event.values[0]
        currentY = event.values[1]
        currentZ = event.values[2]
        Log.d("GyroscopeSensor", "Gyroscope: x=$currentX, y=$currentY, z=$currentZ")
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    // 리스너 해제 메서드
    fun stop() {
        sensorManager?.unregisterListener(this)
    }

    // 외부에서 각각의 축 데이터를 개별적으로 가져올 수 있는 메서드
    fun getCurrentX(): Float = currentX
    fun getCurrentY(): Float = currentY
    fun getCurrentZ(): Float = currentZ

    // 외부에서 현재 자이로스코프 값을 얻을 수 있는 메서드
    fun getCurrentGyroscopeValues(): Triple<Float, Float, Float> {
        return Triple(currentX, currentY, currentZ)
    }
}
