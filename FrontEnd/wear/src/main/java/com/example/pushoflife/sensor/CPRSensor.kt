package com.example.pushoflife.sensor

import kotlin.math.sqrt
import kotlin.math.sign
import kotlin.math.max
import kotlin.math.min
import android.util.Log
import kotlin.math.acos
import com.example.pushoflife.sensor.utils.CPRData

class CPRSensor(private val sampleRate: Float = 100f, private val movingAvgWindowSize: Int = 5) {
    private var lastTimestamp: Long? = null  // 초기에는 null로 설정
    private var lastDepth: Float = 0f
    private var filteredAcc: Float = 0f
    private var gravityCalibration: Float = 0f
    private var isCalibrated = false
    private val alpha = 0.6f  // Low-pass filter coefficient
    private val maxDisplacement = 6f  // 최대 깊이 6cm로 제한
    private val minDisplacement = 0f
    private var lastDisplacement = 0f
    private val fixedH = 9.80665
    private val fixedF = 0.0
    private val fixedG = 0.0
    private val frequencyTimes = mutableListOf<Float>()
    private val depthList = mutableListOf<Float>()
    // 이동 평균 필터용 데이터 버퍼
    private val depthBuffer = ArrayDeque<Float>(movingAvgWindowSize)
    private var lastTime: Long? = null
    private var pussing = false
    private var depthPussing = false
    private var maxDepth = 0.0f
    private var trigger_init = false
    private var total_inv: Double = 0.0
    // 칼만 필터 관련 변수들
    private var state = floatArrayOf(0f, 0f)  // 초기 상태 [displacement, velocity]
    private var P = arrayOf(floatArrayOf(1f, 0f), floatArrayOf(0f, 1f))  // 공분산 행렬
    private val Q = arrayOf(floatArrayOf(0.15f, 0f), floatArrayOf(0f, 0.15f))  // 프로세스 노이즈
    private val R = arrayOf(floatArrayOf(0.05f, 0f), floatArrayOf(0f, 0.05f))  // 측정 노이즈
    private var angle_cnt = 0
    // 중력 보정된 가속도 계산
    private fun calculateGravityCompensatedAcc(accelerometer: FloatArray, gravity: FloatArray): Float {
        val dotProduct = accelerometer[0] * gravity[0] + accelerometer[1] * gravity[1] + accelerometer[2] * gravity[2]
        val gravityMagnitude = sqrt(gravity[0] * gravity[0] + gravity[1] * gravity[1] + gravity[2] * gravity[2])

        // gravityMagnitude가 0이면 NaN 방지
        if (gravityMagnitude == 0f) {
            Log.e("CPRSensor", "Gravity magnitude is zero, cannot calculate compensated acceleration")
            return 0f
        }

        return dotProduct / gravityMagnitude
    }

    // 저주파 필터 적용
    private fun lowPassFilter(rawAcc: Float): Float {
        filteredAcc = alpha * rawAcc + (1 - alpha) * filteredAcc
        return filteredAcc
    }

    // 이동 평균 필터 적용
    private fun applyMovingAverageFilter(depth: Float): Float {
        // depthBuffer에 값 추가
        if (depthBuffer.size == movingAvgWindowSize) {
            depthBuffer.removeFirst()
        }
        depthBuffer.addLast(depth)

        // 평균 계산
        return depthBuffer.average().toFloat()
    }

    // 칼만 필터 적용
    private fun kalmanFilter(measurement: FloatArray): Float {
        val dt = 1f / sampleRate
        val F = arrayOf(floatArrayOf(1f, dt), floatArrayOf(0f, 1f))

        // 예측 단계
        val predictedState = floatArrayOf(
            F[0][0] * state[0] + F[0][1] * state[1],
            F[1][0] * state[0] + F[1][1] * state[1]
        )

        val predictedP = arrayOf(
            floatArrayOf(F[0][0] * P[0][0] + F[0][1] * P[1][0] + Q[0][0], F[0][0] * P[0][1] + F[0][1] * P[1][1]),
            floatArrayOf(F[1][0] * P[0][0] + F[1][1] * P[1][0], F[1][0] * P[0][1] + F[1][1] * P[1][1] + Q[1][1])
        )

        // 업데이트 단계
        val H = arrayOf(floatArrayOf(1f, 0f), floatArrayOf(0f, 1f))
        val S = arrayOf(
            floatArrayOf(predictedP[0][0] + R[0][0], predictedP[0][1] + R[0][1]),
            floatArrayOf(predictedP[1][0] + R[1][0], predictedP[1][1] + R[1][1])
        )

        val K = arrayOf(
            floatArrayOf(predictedP[0][0] / S[0][0], predictedP[0][1] / S[1][1]),
            floatArrayOf(predictedP[1][0] / S[0][0], predictedP[1][1] / S[1][1])
        )

        val y = floatArrayOf(measurement[0] - H[0][0] * predictedState[0], measurement[1] - H[1][1] * predictedState[1])
        state[0] = predictedState[0] + K[0][0] * y[0]
        state[1] = predictedState[1] + K[1][1] * y[1]
        P = arrayOf(
            floatArrayOf((1 - K[0][0]) * predictedP[0][0], (1 - K[0][1]) * predictedP[0][1]),
            floatArrayOf((1 - K[1][0]) * predictedP[1][0], (1 - K[1][1]) * predictedP[1][1])
        )

        return state[0]  // 변위 값 반환
    }

    // 압박 깊이 계산
    fun feedbackCPR(accelerometer: FloatArray, gravity: FloatArray): CPRData {
        val currentTimestamp = System.currentTimeMillis()

        // 초기 호출 시 타임스탬프 설정
        if (lastTimestamp == null) {
            lastTimestamp = currentTimestamp
            return CPRData(0, 0, 0, 0.0f) // 첫 번째 호출 시 초기값 반환
        }

        // dt 계산 및 초기값 설정
        val dt = max((currentTimestamp - lastTimestamp!!) / 1000.0f, 0.001f)  // 최소값을 0.001초로 설정
        lastTimestamp = currentTimestamp

        // 중력 보정된 가속도 값 계산
        val accZ = calculateGravityCompensatedAcc(accelerometer, gravity)

        // 보정된 가속도 필터링
        val linearAcc = lowPassFilter(accZ)
        //Log.d("깊이 가속도", "가속도 : $linearAcc")

        // 속도 계산
        val velocity = linearAcc * dt

        // 이동 평균 필터 적용
        val depthAfterMovingAvg = applyMovingAverageFilter(velocity)

        // 칼만 필터 적용
        val measurement = floatArrayOf(depthAfterMovingAvg, linearAcc)
        var displacement = kalmanFilter(measurement)

        // 변위 범위 제한
        displacement = max(min(displacement, maxDisplacement), minDisplacement)

        // 순간적인 변화 제한 (5cm 제한)
        if (kotlin.math.abs(displacement - lastDisplacement) > 0.05f) {
            displacement = lastDisplacement + sign(displacement - lastDisplacement) * 0.05f
        }

        lastDisplacement = displacement
        lastDepth = displacement
//        Log.d("깊이 찍기", "깊이 : ${displacement*100}")
        val temp_frequency = frequencyCPR(displacement*100)
        val temp_depth = depthCPR(displacement*100)
        val temp_angle = calculateAngle(gravity[0].toDouble(),gravity[1].toDouble(),gravity[2].toDouble())

        return CPRData(temp_depth, temp_frequency, temp_angle, linearAcc)// Convert to cm
    }

    private fun frequencyCPR(displacement: Float): Int {
        if (displacement > 2.0f && !pussing) {
            pussing = true
            val currentTime = System.currentTimeMillis()

            if (lastTime == null) {
                lastTime = currentTime
                return 0 // "측정중이에요"  // 첫 번째 호출 시 메시지 반환
            }

            val dt = (currentTime - lastTime!!) / 1000f
            if (0.2 < dt) {
                frequencyTimes.add(dt)  // frequencyTimes에 dt 값 추가
            }
            lastTime = currentTime

            if (!trigger_init && frequencyTimes.size >= 10 && depthList.size >= 10) {
                trigger_init = true
                val avgDt = frequencyTimes.average()
                Log.d("빈도 측정","frequency : ${frequencyTimes.toString()}")
                frequencyTimes.clear()  // 평균을 계산 후 리스트 초기화

                return if (avgDt in 0.45..0.65) {
                    2 //"잘하고 있어요"  // 평균이 0.5초~0.6초일 때
                } else if (avgDt > 0.65 ){
                    1 //"느려요"  // 평균이 0.5초~0.6초 범위를 벗어날 때
                } else {
                    3 //"빨라요"
                }
            }
        } else if (displacement == 0f && pussing) {
            pussing = false
        }
        return 0 //"측정중이에요"  // 리스트가 다 채워지지 않았을 때
    }

    private fun depthCPR(displacement: Float): Int {
        if (displacement != 0.0f && depthPussing) {
            if (maxDepth < displacement) {
                maxDepth = displacement
            }
        } else if (displacement == 0.0f) {
            depthPussing = true
            if (maxDepth != 0.0f) {
                depthList.add(maxDepth)
                depthPussing = false
            }
            maxDepth = 0.0f
        }
        if (trigger_init) {
            val avgDepth = depthList.average()
            //Log.d("빈도 측정","depth : ${depthList.toString()}")
            depthList.clear()
            return if (avgDepth > 5.0){
                2 // 적당한 깊이로 잘하고 있어요
            } else {
                1 // 깊이가 부족해요
            }
        }
        return 0
    }

    // 3D 벡터 각도 계산 함수
    private fun calculateAngle(c: Double, d: Double, e: Double): Int {
        val s = (c * fixedF) + (d * fixedG) + (e * fixedH)
        val t = sqrt((c * c) + (d * d) + (e * e))
        val r = sqrt((fixedF * fixedF) + (fixedG * fixedG) + (fixedH * fixedH))
        val u = s / (t * r)
        val inv = Math.toDegrees(acos(u)).toDouble()

        if (trigger_init) {
            trigger_init = false
            // angle_cnt가 0이 아닌지 확인
            val inv_avg = if (angle_cnt != 0) total_inv / angle_cnt else 0.0
            total_inv = 0.0
            angle_cnt = 0
            //Log.d("빈도 측정", "angle : $inv_avg")
            return if (inv_avg in 75.0..105.0) {
                2
            } else {
                1
            }
        } else {
            total_inv += inv
            angle_cnt += 1
            return 0
        }
    }


}