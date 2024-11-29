package com.example.pushoflife

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.mutableStateOf
import com.example.pushoflife.ui.SecondScreen
import com.example.pushoflife.network.MessageSender
import com.google.android.gms.tasks.Task
import com.google.android.gms.wearable.Node
import com.google.android.gms.wearable.NodeClient
import com.google.android.gms.wearable.Wearable
import android.media.MediaPlayer
import android.speech.tts.TextToSpeech
import java.util.Locale

class SecondActivity : ComponentActivity() {

    private lateinit var tts: TextToSpeech
    private lateinit var messageSender: MessageSender
    private var nodeId: String? = null
    private val nodeIdState = mutableStateOf<String?>(null)  // nodeId를 상태로 관리
    private lateinit var sensorServiceIntent: Intent  // Foreground Service Intent 선언
    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        // Foreground Service 중지
//        sensorServiceIntent = Intent(this, SensorForegroundService::class.java)
//        st44opService(sensorServiceIntent) // SecondActivity에 진입할 때 Foreground Service 종료

        // 화면 밝기를 최대로 설정
        window.attributes = window.attributes.apply {
            screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL
        }

        // 화면이 절대 꺼지지 않도록 설a정
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        // MessageSender 초기화
        messageSender = MessageSender(applicationContext)

        // nodeId 가져오기 비동기 작업
        getConnectedNodeId { id ->
            nodeIdState.value = id  // nodeId 상태 업데이트
            //Log.d("NodeID", "Connected node ID: ${nodeIdState.value}")
        }

        // MediaPlayer 초기화 및 사이렌 재생
        mediaPlayer = MediaPlayer.create(this, R.raw.alarm_sound).apply {
            isLooping = true  // 반복 재생
            start()  // 재생 시작
        }

        // Compose UI 설정
        setContent {
            if (nodeIdState.value == null) {
//                // 로딩 화면을 보여줌
//                LoadingScreen()
            } else {
                // nodeId가 설정된 후 SecondScreen을 표시
                SecondScreen(
                    nodeId = nodeIdState.value,
                    messageSender = messageSender,
                    onNavigateToCprGuide = {
                        navigateToCprGuide()
                    }
                )
            }
        }
    }
    private fun navigateToCprGuide() {
        val intent = Intent(this, CprGuideActivity::class.java).apply {
            putExtra("previous_activity", "SecondActivity")  // 이전 액티비티 정보 추가
        }
        startActivity(intent)
        finish() // 현재 액티비티 종료
    }
    // 연결된 노드 ID를 가져오는 메서드
    private fun getConnectedNodeId(onNodeIdReceived: (String?) -> Unit) {
        val nodeClient: NodeClient = Wearable.getNodeClient(this)
        val nodeListTask: Task<List<Node>> = nodeClient.connectedNodes

        nodeListTask.addOnSuccessListener { nodes ->
            val nodeId = nodes.firstOrNull()?.id
            onNodeIdReceived(nodeId)
        }.addOnFailureListener {
            Log.e("NodeID", "Failed to get connected node ID", it)
            onNodeIdReceived(null)
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        // MediaPlayer 자원 해제
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
