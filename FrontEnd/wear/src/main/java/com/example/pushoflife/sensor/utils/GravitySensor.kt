package com.example.pushoflife.sensor.utils

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log

object GravitySensor : SensorEventListener {
    private var sensorManager: SensorManager? = null
    private var gravitySensor: Sensor? = null
    private var currentX: Float = 0.0f
    private var currentY: Float = 0.0f
    private var currentZ: Float = 0.0f

    // 리스너 인터페이스 정의
    interface GravitySensorListener {
        fun onGravityDataChanged(gravityValues: FloatArray)
    }

    private var gravityListener: GravitySensorListener? = null


    // GravitySensor 초기화 및 리스너 등록
    fun initialize(context: Context, listener: GravitySensorListener) {
        gravityListener = listener  // 리스너 설정
        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        gravitySensor = sensorManager?.getDefaultSensor(Sensor.TYPE_GRAVITY)
        gravitySensor?.let {
            sensorManager?.registerListener(this, it, 10000)
        }
    }

    // 센서 데이터가 업데이트될 때마다 호출
    override fun onSensorChanged(event: SensorEvent) {
        currentX = event.values[0]
        currentY = event.values[1]
        currentZ = event.values[2]
//        Log.d("GravitySensor", "Gravity: x=$currentX, y=$currentY, z=$currentZ")

        // 리스너를 통해 업데이트 알림
        gravityListener?.onGravityDataChanged(floatArrayOf(currentX, currentY, currentZ))
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

    // 전체 중력 값 배열을 반환하는 메서드
    fun getCurrentGravityValues(): FloatArray = floatArrayOf(currentX, currentY, currentZ)
}
