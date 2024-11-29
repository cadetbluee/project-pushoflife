package com.example.pushoflife

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.mutableStateOf
import com.example.pushoflife.network.MessageSender
import com.example.pushoflife.ui.CprWaitingScreen
import com.example.pushoflife.ui.SecondScreen
import com.google.android.gms.tasks.Task
import com.google.android.gms.wearable.Node
import com.google.android.gms.wearable.NodeClient
import com.google.android.gms.wearable.Wearable

class FirstActivity : ComponentActivity() {
    private lateinit var messageSender: MessageSender
    private val nodeIdState = mutableStateOf<String?>(null)  // nodeId를 상태로 관리
    private val handler = Handler(Looper.getMainLooper()) // Handler 생성
    private lateinit var sensorServiceIntent: Intent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 화면 밝기를 최대로 설정
        window.attributes = window.attributes.apply {
            screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL
        }

        // 화면이 절대 꺼지지 않도록 설정
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        // MessageSender 초기화
        messageSender = MessageSender(applicationContext)
        // Foreground Service 중지
        sensorServiceIntent = Intent(this, SensorForegroundService::class.java)
        stopService(sensorServiceIntent) // SecondActivity에 진입할 때 Foreground Service 종료

        // nodeId 가져오기 비동기 작업
        getConnectedNodeId { id ->
            nodeIdState.value = id  // nodeId 상태 업데이트
            //Log.d("NodeID", "Connected node ID: ${nodeIdState.value}")
        }

        // Compose UI 설정
        setContent {
            CprWaitingScreen(
                nodeId = nodeIdState.value,  // nodeId 상태 전달
                messageSender = messageSender,
                onNavigateToCprGuide = {
                    navigateToMainActivity()  // 버튼 클릭 시 MainActivity로 이동
                }
            )
        }

        // 5초 후 자동으로 SecondActivity로 전환
        handler.postDelayed({
            navigateToSecondActivity()  // 자동 전환
        }, 5000) // 5000밀리초 = 5초
    }

    // MainActivity로 이동하는 함수 (버튼 클릭 시 호출)
    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish() // 현재 액티비티 종료
    }

    // SecondActivity로 이동하는 함수 (5초 후 자동 호출)
    private fun navigateToSecondActivity() {
        val intent = Intent(this, SecondActivity::class.java)
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
        handler.removeCallbacksAndMessages(null) // 액티비티가 종료되면 Handler 콜백 제거
    }
}
