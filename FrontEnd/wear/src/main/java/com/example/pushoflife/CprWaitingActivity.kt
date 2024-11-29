package com.example.pushoflife

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.pushoflife.ui.CprWaitingScreen
import com.example.pushoflife.network.MessageSender
import com.google.android.gms.tasks.Task
import com.google.android.gms.wearable.Node
import com.google.android.gms.wearable.NodeClient
import com.google.android.gms.wearable.Wearable

class CprWaitingActivity : ComponentActivity() {

    private lateinit var messageSender: MessageSender
    private var nodeId: String? = null
    private lateinit var sensorServiceIntent: Intent
    private val handler = Handler(Looper.getMainLooper()) // Handler 생성

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


        // nodeId 가져오기
        getConnectedNodeId { id ->
            nodeId = id
            //Log.d("NodeID", "Second Connected node ID: $nodeId")
        }

        // Compose UI 설정
        setContent {
            CprWaitingScreen(
                nodeId = nodeId,
                messageSender = messageSender,
                onNavigateToCprGuide = {
                    navigateToMain()  // 버튼 클릭 시 MainActivity로 이동
                }
            )
        }

        // 5초 후 자동으로 CPRGuideActivity로 전환
        handler.postDelayed({
            navigateToCprGuide()  // 자동 전환
        }, 5000) // 5000밀리초 = 5초
    }

    // CPRGuideActivity로 이동하는 함수
    private fun navigateToCprGuide() {
        val intent = Intent(this, CprGuideActivity::class.java).apply {
            putExtra("previous_activity", "CprWaitingActivity")  // 이전 액티비티 정보 추가
        }
        startActivity(intent)
        finish() // 현재 액티비티 종료
    }

    // MainActivity로 이동하는 함수
    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
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
