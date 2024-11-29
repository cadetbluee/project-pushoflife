package com.example.pushoflife.sensor.utils

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log

object LinearAccelerationSensor : SensorEventListener {
    private var sensorManager: SensorManager? = null
    private var linearAccelerationSensor: Sensor? = null
    private var currentX: Float = 0.0f
    private var currentY: Float = 0.0f
    private var currentZ: Float = 0.0f
    private var listener: LinearAccelerationListener? = null  // 리스너 추가

    // 리스너 인터페이스 정의
    interface LinearAccelerationListener {
        fun onLinearAccelerationDataChanged(accelerationValues: FloatArray)
    }

    // 리스너 초기화
    fun initialize(context: Context, listener: LinearAccelerationListener) {
        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        linearAccelerationSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)
        this.listener = listener  // 리스너 설정
        linearAccelerationSensor?.let {
            sensorManager?.registerListener(this, it, 10000)
        }
    }

    // 센서 데이터가 업데이트될 때마다 호출
    override fun onSensorChanged(event: SensorEvent) {
        currentX = event.values[0]
        currentY = event.values[1]
        currentZ = event.values[2]
//        Log.d("LinearAccelerationSensor", "Acceleration: x=$currentX, y=$currentY, z=$currentZ")

        // 리스너를 통해 외부에 데이터 전달
        listener?.onLinearAccelerationDataChanged(getCurrentAccelerationValues())
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    // 리스너 해제 메서드
    fun stop() {
        sensorManager?.unregisterListener(this)
    }

    // 외부에서 현재 가속도 값을 개별 축으로 가져오는 메서드
    fun getCurrentX(): Float = currentX
    fun getCurrentY(): Float = currentY
    fun getCurrentZ(): Float = currentZ

    // 전체 가속도 값을 배열로 반환하는 메서드
    fun getCurrentAccelerationValues(): FloatArray = floatArrayOf(currentX, currentY, currentZ)
}
