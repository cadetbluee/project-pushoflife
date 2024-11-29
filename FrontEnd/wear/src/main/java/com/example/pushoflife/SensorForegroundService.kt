
package com.example.pushoflife

import android.os.Binder
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.provider.Settings
import android.util.Log
import kotlinx.coroutines.*
import com.example.pushoflife.sensor.FallSensor
import com.example.pushoflife.network.MessageSender
import com.example.pushoflife.utils.LaunchApp
import com.example.pushoflife.sensor.WatchVibrator
import com.google.android.gms.tasks.Task
import com.google.android.gms.wearable.Node
import com.google.android.gms.wearable.NodeClient
import com.google.android.gms.wearable.Wearable
class SensorForegroundService : Service(), SensorEventListener {

    private lateinit var messageSender: MessageSender // lateinit으로 변경하여 지연 초기화
    private val serviceScope = CoroutineScope(Dispatchers.Default + Job())
    private val binder = SensorBinder()
    private lateinit var sensorManager: SensorManager
    private val fallSensor = FallSensor()
    private var possibleFall = false
    private var possibleHeart = false
    private var heartRateSensor: Sensor? = null
    private var gravitySensor: Sensor? = null
    private var linearAccelerationSensor: Sensor? = null
    var gravityValues: FloatArray? = null
    private var totalAcceleration: Float = 0.0f
    private var vibrate = WatchVibrator
    private var nodeId: String? = null


    override fun onCreate() {
        super.onCreate()
        vibrate.initialize(applicationContext)
        // 이제 onCreate 내에서 초기화합니다.
        messageSender = MessageSender(applicationContext)
        // nodeId 가져오기
        getConnectedNodeId(applicationContext) { id ->
            nodeId = id
            //Log.d("NodeID", "Connected node ID: $nodeId")
        }
        // Foreground 알림 생성
        createNotificationChannel()
        createHighPriorityNotificationChannel()
        val notification: Notification = Notification.Builder(this, "sensor_channel")
            .setContentTitle("센서 서비스")
            .setContentText("백그라운드에서 센서 데이터를 수집 중")
            .setSmallIcon(com.example.pushoflife.R.mipmap.ic_launcher_round)
            .build()

        startForeground(1, notification)

        // SensorManager 초기화
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        // 센서 등록 (TYPE_GRAVITY와 TYPE_LINEAR_ACCELERATION 추가)
        heartRateSensor = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE)
        gravitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY)
        linearAccelerationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)

        // 센서 리스너 등록
        heartRateSensor?.also {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
        gravitySensor?.also {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
        linearAccelerationSensor?.also {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }

        // 백그라운드 작업 시작 (센서 데이터 수집은 센서 이벤트에서 처리됨)
        startBackgroundTask()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY  // 서비스가 종료되지 않도록 유지
    }

    // 센서 이벤트 리스너
    override fun onSensorChanged(event: SensorEvent) {
        when (event.sensor.type) {
            Sensor.TYPE_GRAVITY -> {
                //Log.d("SensorData", "gravity: x=$x, y=$y, z=$z")
                gravityValues = event.values.clone()
            }
            Sensor.TYPE_LINEAR_ACCELERATION -> {
//                val currentTime = System.currentTimeMillis()
//
//                if (currentTime - lastLinearAccelerationUpdateTime >= 50 ){
//                    lastLinearAccelerationUpdateTime = currentTime
//                }
                gravityValues?.let { gravity ->
                    totalAcceleration = fallSensor.calculateAcceleration(event.values, gravity)
                    //Log.d("밑 가속도", "total gravity : $totalAccelation"
                }
                //Log.d("너를 통해 얻을거야", "Accelerometer: $totalAcceleration")
                if (!possibleFall && fallSensor.checkFall(totalAcceleration)) {
                    //Log.d("FallDetection", "낙상감지됨")
                    vibrate.vibrate()
                    possibleFall = true
                    // 5초 후 possibleFall을 false로 설정
                    serviceScope.launch {
                        delay(5000L)  // 5초 대기
                        possibleFall = false
                        // Log.d("possibleFall", possibleFall.toString())
                        //Log.d("FallDetection", "possibleFall 초기화됨")
                    }
                }
            }
            Sensor.TYPE_HEART_RATE -> {
                if(possibleFall && event.values[0] < 30.0f && !possibleHeart){
                    // 낙상 감지 + 심정지 확인 후 앱 실행 및 화면 켜기
                    possibleHeart = true
                    nodeId?.let {
                        messageSender.sendMessages(
                            nodeId = it,
                            path = "/emergency_path",
                            message = "심정지 상황 발생"
                        )
                    }
                    LaunchApp.launchApp(this, FirstActivity::class.java)
                    stopSelf()
                    possibleHeart = false
                }
                //Log.d("SensorData", "Heart Rate: $heartRate bpm")
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // 정확도 변경 시 처리할 내용이 있을 경우 여기에 추가
    }

    private fun startBackgroundTask() {
        serviceScope.launch {
            while (isActive) {
                delay(5000L)  // 필요에 따라 백그라운드 작업 수행 (예: 주기적으로 상태 확인)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        sensorManager.unregisterListener(this)  // 센서 리스너 해제
        serviceScope.cancel()  // 백그라운드 작업 취소
        stopForeground(true)
    }
    inner class SensorBinder : Binder() {
        fun getService(): SensorForegroundService = this@SensorForegroundService
    }
    override fun onBind(intent: Intent?): IBinder? {
        return binder
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "sensor_channel"//"notification_channel_id"
            val channelName = "Watch Notification Channel"
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(channelId, channelName, importance)
            // 알림 채널의 소리와 진동 비활성화
            channel.setSound(null, null) // 소리 제거
            channel.enableVibration(false) // 진동 제거
            // NotificationManager에 채널을 등록
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)

            // 채널 생성 확인용 로그
            //Log.d("NotificationChannel", "Notification channel created with ID: $channelId")
        }
    }
    private fun createHighPriorityNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "sensor_high_priority_channel"
            val channelName = "High Priority Notifications"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelId, channelName, importance)

            // 소리와 진동 설정
            channel.enableVibration(true)
            channel.setSound(
                Settings.System.DEFAULT_NOTIFICATION_URI,
                Notification.AUDIO_ATTRIBUTES_DEFAULT
            )

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }


    // 화면을 강제로 켜는 메서드
    private fun turnOnScreen() {
        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        val wakeLock = powerManager.newWakeLock(
            PowerManager.SCREEN_BRIGHT_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP,
            "MyApp::MyWakeLock"
        )
        wakeLock.acquire(10 * 60 * 1000L /*10분 동안 화면을 유지*/)
    }
    fun getConnectedNodeId(context: Context, onNodeIdReceived: (String?) -> Unit) {
        val nodeClient: NodeClient = Wearable.getNodeClient(context)
        val nodeListTask: Task<List<Node>> = nodeClient.connectedNodes

        nodeListTask.addOnSuccessListener { nodes ->
            // 첫 번째 노드의 ID를 사용하거나, 연결된 모든 노드의 목록을 확인할 수 있습니다.
            val nodeId = nodes.firstOrNull()?.id
            onNodeIdReceived(nodeId) // nodeId를 콜백으로 전달
        }.addOnFailureListener {
            Log.e("NodeID", "Failed to get connected node ID", it)
            onNodeIdReceived(null)
        }
    }
}
