package com.example.pushoflife.network

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.pushoflife.CprGuideActivity
import com.example.pushoflife.MainActivity
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService
import com.example.pushoflife.SecondActivity
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMapItem
import org.json.JSONObject
import com.example.pushoflife.data.datastore.EmergencyPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
class WatchListenerService : WearableListenerService() {

    override fun onCreate() {
        super.onCreate()
        Log.d("WatchListenerService", "Service is now waiting for messages...")
    }


    // 작동 하는 코드
    override fun onDataChanged(dataEvents: DataEventBuffer) {
        for (event in dataEvents) {
            if (event.type == DataEvent.TYPE_CHANGED) {
                val dataItem = event.dataItem
                Log.d("Wearable","$dataItem")
                if (dataItem.uri.path == "/notification_path") {
                    // 회원 정보 수정에서 data값이 변경되었을때
                    val dataMap = DataMapItem.fromDataItem(dataItem).dataMap
                    val value = dataMap.getString("name")
                    Log.d("Wearable", "Received data: $value")
                }else if (dataItem.uri.path == "/emergency_location") {
                    val dataMap = DataMapItem.fromDataItem(dataItem).dataMap
                    val latitude = dataMap.getDouble("latitude")
                    val longitude = dataMap.getDouble("longitude")
                    val trigger = dataMap.getBoolean("trigger")

                    Log.d("WatchApp","$trigger")

                    if (trigger){
                        val message = "CPR가이드를 시작합니다"
                        Log.d("WatchApp","feedback start ")
                        showCPRNotification(message)
                    }else{

                    Log.d("WatchApp", "Received latitude: $latitude, longitude: $longitude")

                    // DataStore에 위치 정보 저장
                    val emergencyPreferences = EmergencyPreferences(this)
                    CoroutineScope(Dispatchers.IO).launch {
                        emergencyPreferences.saveLocation(latitude, longitude)
                    }
                    // 위치 정보 알림 표시
                    val message = "근처에 도움이 필요한 환자가 발생했습니다"
                    showNotification(message)
                    }

                } else if (dataItem.uri.path == "/feedback_start"){
                    val dataMap = DataMapItem.fromDataItem(dataItem).dataMap
                    val trigger = dataMap.getBoolean("trigger")
                    Log.d("WatchApp", "$trigger")

                    val message = "CPR가이드를 시작합니다"
                    Log.d("WatchApp","feedback start ")
                    showCPRNotification(message)
                }
            }
        }
    }




    // 작동 안 하는 코드
    override fun onMessageReceived(messageEvent: MessageEvent) {
        Log.d("WatchListenerService", "Message received with path: ${messageEvent.path}")

        if (messageEvent.path == "/notification_path") {
            val message = String(messageEvent.data) // 폰에서 보낸 메시지 내용
            Log.d("WatchListenerService", "Message content: $message")
            // 알림 띄우기
            showNotification(message)
            // 모바일에서 응급 환자 발생했다는 알림을 주면 그 알림을 눌러서 지도 켜지게
        } else if (messageEvent.path == "/emergency_location") {
            val dataString = String(messageEvent.data)
            val json = JSONObject(dataString)

            val latitude = json.getDouble("latitude")
            val longitude = json.getDouble("longitude")

            Log.d("WatchApp", "Received latitude: $latitude, longitude: $longitude")
        }
    }

    private fun showNotification(message: String) {
        val notificationId = 1
        val channelId = "sensor_high_priority_channel" // SensorForegroundService에서 생성한 알림 채널 ID

        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("구조 요청 알림")
            .setContentText(message)
            .setSmallIcon(com.example.pushoflife.R.mipmap.ic_launcher)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(this).notify(notificationId, notification)
    }
    private fun showCPRNotification(message: String) {
        val notificationId = 2
        val channelId = "sensor_high_priority_channel" // 새로 만든 중요도 높은 알림 채널 ID

        // CprGuideActivity를 시작하는 Intent 생성
        val intent = Intent(this, CprGuideActivity::class.java).apply {
            putExtra("previous_activity", "fromNotification")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        startActivity(intent) // CprGuideActivity 직접 실행

        // 알림 생성
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("CPR 가이드")
            .setContentText(message)
            .setSmallIcon(com.example.pushoflife.R.mipmap.ic_launcher)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(this).notify(notificationId, notification)
    }

}
