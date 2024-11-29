package com.example.pushoflife.sensor

class FallSensor {

    // 낙상 감지를 위한 가속도 값 확인
    fun checkFall(totalAcceleration: Float): Boolean {
        return totalAcceleration > 25  // 특정 임계값 이상이면 낙상 감지로 판단
    }

    // 중력 방향으로 가해지는 가속도 성분 계산
    fun calculateAcceleration(linearAcceleration: FloatArray, gravity: FloatArray): Float {
        // 내적 계산을 통해 중력 방향으로 가해지는 가속도 성분을 구함
        val dotProduct = linearAcceleration[0] * gravity[0] +
                linearAcceleration[1] * gravity[1] +
                linearAcceleration[2] * gravity[2]

        // 중력 벡터의 크기 계산
        val gravityMagnitude = Math.sqrt(
            (gravity[0] * gravity[0] + gravity[1] * gravity[1] + gravity[2] * gravity[2]).toDouble()
        ).toFloat()

        // 중력 방향으로 가해지는 가속도 반환
        return dotProduct / gravityMagnitude
    }

    // 심박수가 특정 범위에 들어가는지 확인하여 이상 상태 감지
    fun checkHeart(heartRate: Float): Boolean {
        return heartRate > 0 && heartRate < 30  // 심박수가 너무 낮을 때 이상 상태로 판단
    }
}
