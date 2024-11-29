package com.example.pushoflife

import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import android.util.Log
import androidx.compose.runtime.*
import com.example.pushoflife.ui.CprGuideScreen
import kotlinx.coroutines.*
import java.util.*
import com.example.pushoflife.sensor.utils.GravitySensor
import com.example.pushoflife.sensor.utils.LinearAccelerationSensor
import com.example.pushoflife.sensor.CPRSensor
import android.view.WindowManager

class CprGuideActivity : ComponentActivity(), TextToSpeech.OnInitListener,
    GravitySensor.GravitySensorListener, LinearAccelerationSensor.LinearAccelerationListener {

    private lateinit var cprSensor: CPRSensor
    private lateinit var tts: TextToSpeech
    private var metronomeJob: Job? = null
    private val toneGenerator = ToneGenerator(AudioManager.STREAM_ALARM, 100)

    // Feedback 및 데이터 관련 변수들
    private var feedbackCPR by mutableStateOf(intArrayOf(0, 0, 0))
    private var inputFeedback by mutableStateOf(intArrayOf(0, 0, 0))
    private var feedbackTurn = 1
    private var totalAcceleration by mutableStateOf(0.0f)
    private var latestGravityValues = floatArrayOf(0.0f, 0.0f, 0.0f)
    private var latestAccelerationValues = floatArrayOf(0.0f, 0.0f, 0.0f)
    private lateinit var previousActivity: String
    private var collectSensorData = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        previousActivity = intent.getStringExtra("previous_activity") ?: ""


        tts = TextToSpeech(this, this)

        // Compose UI 설정
        setContent {
            LaunchedEffect(inputFeedback, totalAcceleration) {}

            CprGuideScreen(
                totalAcceleration = totalAcceleration,
                onExitClick = {
                    stopAudio()
                    finish()
                },
                cprFeedback = inputFeedback
            )
        }
    }

    // GravitySensor 데이터 변경 시 호출
    override fun onGravityDataChanged(gravityValues: FloatArray) {
        latestGravityValues = gravityValues
        if (collectSensorData) checkAndUpdateCprFeedback()
    }

    // LinearAccelerationSensor 데이터 변경 시 호출
    override fun onLinearAccelerationDataChanged(accelerationValues: FloatArray) {
        latestAccelerationValues = accelerationValues
        if (collectSensorData) checkAndUpdateCprFeedback()
    }

    // TTS 초기화 후 실행할 작업 정의
    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts.language = Locale.KOREAN
            when (previousActivity) {
                "SecondActivity" -> startCprGuide() // 가이드 TTS 후 메트로놈만
                "fromNotification" -> startSensorDataCollection() // 센서 데이터 수집 및 피드백 TTS만
                "CprWaitingActivity", "MainActivity" -> {
                    startCprGuide() // 가이드 TTS 후 메트로놈 시작, 센서 데이터 수집 포함
                }
            }
        } else {
            Log.e("CprGuideActivity", "TTS 초기화 실패")
        }
    }

    private fun startCprGuide() {
        val guideText = "시작. 무릎을 꿇은 채로 세워 주세요... 박자에 맞춰 5cm 깊이로 흉부 압박 시작합니다. 3,2,1."

        tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {}
            override fun onDone(utteranceId: String?) {
                if (previousActivity == "SecondActivity" || previousActivity == "CprWaitingActivity" || previousActivity == "MainActivity") {
                    runOnUiThread { startMetronome() }
                    if (previousActivity != "SecondActivity") {
                        startSensorDataCollection() // 필요한 경우에만 센서 데이터 수집 시작
                    }
                }
            }
            override fun onError(utteranceId: String?) {}
        })

        tts.speak(guideText, TextToSpeech.QUEUE_FLUSH, null, "CPR_GUIDE")
    }

    private fun startSensorDataCollection() {
        GravitySensor.initialize(this, this)
        LinearAccelerationSensor.initialize(this, this)
        cprSensor = CPRSensor()
        collectSensorData = true
    }

    private fun checkAndUpdateCprFeedback() {
        val tempData = cprSensor.feedbackCPR(latestAccelerationValues, latestGravityValues)
        feedbackCPR = intArrayOf(tempData.depth, tempData.frequency, tempData.angle)
        totalAcceleration = tempData.accZ

        if (feedbackCPR.all { it != 0 }) { // 2.1.1 //
            inputFeedback = feedbackCPR.copyOf()
            processFeedback()
        }
    }

    private fun processFeedback() {
        val priorityOrder = when (feedbackTurn) {
            1 -> listOf(0, 1, 2)
            2 -> listOf(1, 0, 2)
            3 -> listOf(2, 0, 1)
            else -> listOf(0, 1, 2)
        }

        for (i in priorityOrder) {
            when (feedbackCPR[i]) {
                1 -> {
                    val message = when (i) {
                        0 -> "더 깊게 압박해주세요"
                        1 -> "압박 빈도가 느립니다"
                        2 -> "팔을 90도로 세워 주세요"
                        else -> ""
                    }
                    giveFeedback(message)
                    break
                }
                3 -> if (i == 1) {
                    giveFeedback("압박 빈도가 빠릅니다")
                    break
                }
                2 -> if (i == priorityOrder.last() && feedbackCPR.all { it == 2 }) {

                }
            }
        }
        feedbackTurn = (feedbackTurn % 3) + 1
    }

    private fun giveFeedback(message: String) {
        if (!tts.isSpeaking) {
            tts.speak(message, TextToSpeech.QUEUE_FLUSH, null, "CPR_FEEDBACK")
        }
    }

    private fun startMetronome() {
        if (metronomeJob?.isActive == true) return
        val intervalMillis = (60000 / 110).toLong()

        metronomeJob = CoroutineScope(Dispatchers.Main).launch {
            while (isActive) {
                toneGenerator.startTone(ToneGenerator.TONE_PROP_ACK, 100)
                delay(intervalMillis)
            }
        }
    }

    private fun stopAudio() {
        if (tts.isSpeaking) tts.stop()
        tts.shutdown()
        metronomeJob?.cancel()
        toneGenerator.release()
        collectSensorData = false
    }

    override fun onDestroy() {
        super.onDestroy()
        stopAudio()
    }

    override fun onStop() {
        super.onStop()
        stopAudio()
        GravitySensor.stop()
        LinearAccelerationSensor.stop()
    }
}
