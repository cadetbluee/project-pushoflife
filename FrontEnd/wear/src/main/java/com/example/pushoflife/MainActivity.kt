/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter and
 * https://github.com/android/wear-os-samples/tree/main/ComposeAdvanced to find the most up to date
 * changes to the libraries and their usages.
 */

package com.example.pushoflife

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
// import androidx.compose.material.Text  // `Text`는 `androidx.wear.compose.material.Text`로 사용
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.material.*
import com.example.pushoflife.theme.PushOfLifeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PushOfLifeTheme {

                val viewModel: SensorViewModel = viewModel(factory = SensorViewModelFactory(this))

                val accelerometerData by viewModel.accelerometerData.collectAsStateWithLifecycle()
                val gyroscopeData by viewModel.gyroscopeData.collectAsStateWithLifecycle()
                val heartRateData by viewModel.heartRateData.collectAsStateWithLifecycle()

                Scaffold(
                    timeText = {
                        TimeText(timeTextStyle = TimeTextDefaults.timeTextStyle(fontSize = 10.sp))
                    },
                    vignette = { Vignette(vignettePosition = VignettePosition.TopAndBottom) }
                ) {
                    SensorDisplay(
                        accelerometerData = accelerometerData,
                        gyroscopeData = gyroscopeData,
                        heartRateData = heartRateData,
                        onCPRButtonClick = {
                            // CPR 피드백 화면으로 이동
                            startCPRGuideActivity()
                        }
                    )
                }
            }
        }
    }

    private fun startCPRGuideActivity() {
        val intent = Intent(this, CPRGuideActivity::class.java)
        // 새로운 Activity로 전환 시 현재 Activity를 유지하고 뒤로 가지 않도록 설정
        startActivity(intent)
    }
}

@Composable
fun SensorDisplay(
    accelerometerData: String,
    gyroscopeData: String,
    heartRateData: String,
    onCPRButtonClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = accelerometerData, fontSize = 16.sp)

        Spacer(modifier = Modifier.height(16.dp))
        Text(text = gyroscopeData, fontSize = 16.sp)

        Spacer(modifier = Modifier.height(16.dp))
        Text(text = heartRateData, fontSize = 16.sp)

        Spacer(modifier = Modifier.height(16.dp))

        // 빨간 버튼 추가 (CPR 시작)
        Button(
            onClick = onCPRButtonClick,
            colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.error)
        ) {
            Text(text = "Start CPR", color = MaterialTheme.colors.onError)
        }
    }
}
