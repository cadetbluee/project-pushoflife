package com.example.pushoflife.service
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.AudioManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.pushoflife.MainActivity
import com.example.pushoflife.R
import android.media.MediaPlayer
import android.media.ToneGenerator
import android.os.Handler
import android.os.Looper
import android.os.Vibrator
import android.os.VibrationEffect
import com.example.pushoflife.BuildConfig
import com.example.pushoflife.data.datastore.TokenPreferences
import com.example.pushoflife.data.datastore.UserPreferences
import com.example.pushoflife.network.RetrofitClient
import com.example.pushoflife.network.SendLocationRequest
import com.example.pushoflife.utils.TwilioUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.view.GestureDetector
import android.view.MotionEvent
import kotlinx.coroutines.*
import org.w3c.dom.Text
import java.util.*

private var isMovementDetected = false
private var isFallDetected = false
private var isTTSStart = false
private val handler = Handler(Looper.getMainLooper())

class FallDetectionService : Service(), SensorEventListener, TextToSpeech.OnInitListener {

    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var vibrator: Vibrator
    private lateinit var gravitySensor: Sensor
    private lateinit var linearAccelerationSensor: Sensor
    private val gravityValues = FloatArray(3)
    private val linearAccelerationValues = FloatArray(3)
    private val sirenHandler = Handler(Looper.getMainLooper())
    private lateinit var tts: TextToSpeech
    private var metronomeJob: Job? = null
    private val toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 100)
    private lateinit var gestureDetector: GestureDetector

    private val sirenRunnable = object : Runnable {
        override fun run() {
            if (isFallDetected && !isMovementDetected) {
                // 진동 시작
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(
                        VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE)
                    )
                } else {
                    vibrator.vibrate(1000)
                }

                // 알림을 HIGH 중요도로 생성하여 알림창에 표시
                val notification = createHighPriorityNotification()
                val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.notify(2, notification)

                // 반복 실행 - 1초 대기 후 다시 실행
                sirenHandler.postDelayed(this, 1000)


            }
        }
    }
    override fun onCreate() {
        super.onCreate()
        // 센서 매니저 초기화
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        // 중력 센서와 선형 가속도 센서 초기화
        gravitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY) ?: throw IllegalStateException("Gravity sensor not available")
        linearAccelerationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION) ?: throw IllegalStateException("Linear acceleration sensor not available")

        // 센서 리스너 등록
        sensorManager.registerListener(this, gravitySensor, SensorManager.SENSOR_DELAY_NORMAL)
        sensorManager.registerListener(this, linearAccelerationSensor, SensorManager.SENSOR_DELAY_NORMAL)

        // MediaPlayer 초기화 (사이렌 사운드 파일 추가)
        mediaPlayer = MediaPlayer.create(this, R.raw.alarm_sound) // R.raw.siren_sound는 미리 추가한 사이렌 음원 파일

        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        // 포그라운드 알림 설정
        createNotificationChannel()
        val notification = createNotification()
        startForeground(1, notification)

        tts = TextToSpeech(this, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts.language = Locale.KOREAN
            // startCprGuide() // TTS 초기화가 완료되면 안내문구 재생 시작
        } else {
            Log.e("SensorForegroundService", "TTS 초기화 실패")
        }
    }

    private fun startCprGuide() {
        val guideText = "심폐소생술 가이드를 시작합니다. 무릎을 꿇은 채로 세워 주세요. " +
                "손목과 팔의 각도를 90도로 유지해 주세요. 박자에 맞춰 5cm 깊이로 흉부 압박을 시작합니다. " +
                "3, 2, 1."
Log.d("CPR가이드","$guideText")
        tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {}

            override fun onDone(utteranceId: String?) {
                startMetronome() // TTS 완료 후 메트로놈 시작
            }

            override fun onError(utteranceId: String?) {}
        })

        tts.speak(guideText, TextToSpeech.QUEUE_FLUSH, null, "CPR_GUIDE")
    }

    private fun startMetronome() {
        val intervalMillis = (60000 / 110).toLong() // 110 BPM (약 545ms 간격)

        metronomeJob = CoroutineScope(Dispatchers.Main).launch {
            while (isActive) {
                toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP, 100) // 톤 생성
                delay(intervalMillis) // 간격
            }
        }
    }
    private fun stopAudio() {
        tts.apply {
            if (isSpeaking) stop()
            shutdown()
        }
        metronomeJob?.cancel()
        toneGenerator.release() // 리소스 해제
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_STOP_SIREN -> stopSiren()
        }
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        sensorManager.unregisterListener(this)
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onSensorChanged(event: SensorEvent) {
        when (event.sensor.type) {
            Sensor.TYPE_GRAVITY -> {
                // 중력 벡터 저장
                gravityValues[0] = event.values[0]
                gravityValues[1] = event.values[1]
                gravityValues[2] = event.values[2]
            }
            Sensor.TYPE_LINEAR_ACCELERATION -> {
                // 선형 가속도 벡터 저장
                linearAccelerationValues[0] = event.values[0]
                linearAccelerationValues[1] = event.values[1]
                linearAccelerationValues[2] = event.values[2]

                // 중력 기반 가속도 계산
                val acceleration = calculateAcceleration(linearAccelerationValues, gravityValues)

                if (acceleration > 10 && !isFallDetected) { // 임계값 설정
                    isFallDetected = true
                    Log.d("FallDetectionService", "Fall detected! Triggering siren.")
                    triggerSiren()
                } else if (acceleration > 3 && isTTSStart){
                    isTTSStart = false;
                    Log.d("FallDetectionService", "end TTS")
                    stopAudio()

                } else if (acceleration > 3 && isFallDetected) {
                    isMovementDetected = true
                    Log.d("FallDetectionService", "Movement detected! Stopping siren.")
                    stopSirenForHelper()
                    startCprGuide()
                    startCprGuideWithDelay()
                }
            }
        }
    }

    companion object {
        const val ACTION_STOP_SIREN = "com.example.pushoflife.service.ACTION_STOP_SIREN"
    }

    private fun startCprGuideWithDelay() {
        handler.postDelayed({
            isTTSStart = true

        }, 3000) // 3000ms = 3초
    }
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
    private val checkForMovementRunnable = Runnable {
        if (!isMovementDetected) {
            sendEmergencySMS()  // 'this@FallDetectionService' 참조 제거
        }
    }

    private fun triggerSiren() {
        // 사이렌 소리 재생
        if (!mediaPlayer.isPlaying) {
            mediaPlayer.isLooping = true // 소리 반복 재생
            mediaPlayer.start()
        }

        // 진동과 알림을 30초간 반복
        sirenHandler.post(sirenRunnable)
        handler.postDelayed(checkForMovementRunnable, 30_000) // 30초 타이머 설정

    }

    private fun stopSirenForHelper() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
            mediaPlayer.seekTo(0)
        }
        vibrator.cancel()
        handler.postDelayed(checkForMovementRunnable, 30_000) // 30초 타이머 설정


    }
    private fun stopSiren() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
            mediaPlayer.seekTo(0)
        }
        vibrator.cancel()
        // 알림 취소하기
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(2) // 알림 ID에 맞게 설정 (여기서는 2로 설정)

        isFallDetected=false
        isMovementDetected = false

        // 특정 음성 파일 재생


    }
    fun calculateAcceleration(linearAcceleration: FloatArray, gravity: FloatArray): Float {
        val dotProduct = linearAcceleration[0] * gravity[0] +
                linearAcceleration[1] * gravity[1] +
                linearAcceleration[2] * gravity[2]
        // 중력 벡터의 크기
        val gravityMagnitude = Math.sqrt((gravity[0] * gravity[0] + gravity[1] * gravity[1] + gravity[2] * gravity[2]).toDouble()).toFloat()

        // 중력 방향으로 가해지는 가속도 성분
        return dotProduct / gravityMagnitude
    }
    private fun createHighPriorityNotification(): Notification {
        // 알림 클릭 시 특정 페이지로 이동하도록 설정 (예: MainActivity의 특정 화면으로 이동)
        val notificationIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, "FallDetectionChannel")
            .setContentTitle("낙상 감지 !")
            .setContentText("알림을 눌러 종료")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)  // 클릭 시 알림 자동 제거
            .setDefaults(NotificationCompat.DEFAULT_ALL)  // 알림 소리, 진동 설정
            .build()
    }


    private fun createNotification(): Notification {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            notificationIntent,
            PendingIntent.FLAG_IMMUTABLE  // FLAG_IMMUTABLE 추가
        )

        return NotificationCompat.Builder(this, "FallDetectionChannel")
            .setContentTitle("낙상감지 서비스")
            .setContentText("감지 중..")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_LOW)  // 중요도를 낮추어 알림을 최소화
            .setContentIntent(pendingIntent)
            .setSilent(true)  // 소리 없이 알림 표시
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                "FallDetectionChannel",
                "Fall Detection Service Channel",
                NotificationManager.IMPORTANCE_LOW // 서비스 기본 채널은 낮음으로 설정
            )

            val highPriorityChannel = NotificationChannel(
                "HighPriorityFallChannel",
                "High Priority Fall Notifications",
                NotificationManager.IMPORTANCE_HIGH // 낙상 감지 시 사용하는 중요도 높은 채널
            )

            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
            manager.createNotificationChannel(highPriorityChannel)
        }
    }
    // 30초 후 움직임이 없으면 문자 전송
    private fun sendEmergencySMS() {
        CoroutineScope(Dispatchers.IO).launch {
            val userPreferences = UserPreferences(this@FallDetectionService)
            val messageContent = userPreferences.emergencyMessage.first() // 저장된 메시지 불러오기
            // Broadcast 전송
            val intent = Intent("com.example.pushoflife.ACTION_WATCH_ALERT")
            sendBroadcast(intent)
            TwilioUtils.sendSmsWithRetrofit(
                "", // 수신자 번호
                BuildConfig.FROM_NUMBER,
                messageContent,
                BuildConfig.TWILIO_ACCOUNT_SID,
                BuildConfig.TWILIO_AUTH_TOKEN
            )
            // DataStore에서 FCM token을 가져와서 API 요청
            GlobalScope.launch(Dispatchers.IO) {
                val tokenPreferences = TokenPreferences(context = applicationContext)
                val fcmToken = tokenPreferences.getFcmToken().first()

                // 서버로 전송할 LocationRequest 객체 생성
                val sendRequest = SendLocationRequest(
                    fcm_token = fcmToken ?: "",
                )

                RetrofitClient.apiService.askForHelp(sendRequest).enqueue(object :
                    Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        if (response.isSuccessful) {
                            Log.d("AskForHelp", "My Location data sent successfully.")
                        } else {
                            Log.e("AskForHelp", "Failed to send location data. Response code: ${response.code()}")
                        }
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        Log.e("AskForHelp", "Error sending location data: ${t.message}")
                    }
                })
            }
            Log.d("FallDetectionService", "Emergency SMS sent: $messageContent")
        }
    }

}
