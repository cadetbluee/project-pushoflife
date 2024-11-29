package com.example.pushoflife.sensor

import android.content.Context
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.Build

object WatchVibrator {

    private var vibrator: Vibrator? = null

    // 초기화 함수
    fun initialize(context: Context) {
        if (vibrator == null) { // 이미 초기화되었는지 확인
            vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
    }

    // 진동 발생 함수
    fun vibrate() {
        vibrator?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                it.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                it.vibrate(500)
            }
        }
    }
}
