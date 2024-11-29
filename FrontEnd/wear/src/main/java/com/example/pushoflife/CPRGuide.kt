package com.example.pushoflife

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
// import androidx.compose.material.Text  // Text는 androidx.wear.compose.material.Text로 사용
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.*

class CPRGuideActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // CPR 피드백 UI
            Scaffold(
                timeText = {
                    TimeText(timeTextStyle = TimeTextDefaults.timeTextStyle(fontSize = 10.sp))
                },
                vignette = { Vignette(vignettePosition = VignettePosition.TopAndBottom) }
            ) {
                CPRGuideScreen()
            }
        }
    }
}

@Composable
fun CPRGuideScreen() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "CPR Feedback", fontSize = 20.sp)

        Spacer(modifier = Modifier.height(16.dp))

        // 센서 데이터를 사용한 CPR 피드백을 여기에 표시
        Text(text = "Analyzing CPR...", fontSize = 16.sp)

        // CPR 피드백 관련 정보를 계속 업데이트
    }
}
