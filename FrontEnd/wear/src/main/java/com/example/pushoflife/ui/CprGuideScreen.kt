package com.example.pushoflife.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import java.util.Arrays
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.util.Log
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import com.example.pushoflife.theme.wearColorPalette
import kotlin.math.max
var screenColor = false

@Composable
fun CprGuideScreen(totalAcceleration: Float, cprFeedback: IntArray, onExitClick: () -> Unit) {
    // 가속도 기록 리스트를 기억
//    Log.d("가속도","$totalAcceleration")
    val accelerationData = remember { mutableListOf<Float>().apply { addAll(List(5) { 0f }) } }

    // 새로운 가속도 값 추가 및 최대 데이터 크기 제한
    if (accelerationData.size > 50) { // 최대 50개 데이터 유지
        accelerationData.removeAt(0)
    }
    if (totalAcceleration>1||totalAcceleration<-1){
        accelerationData.add(totalAcceleration)
    }else{
        accelerationData.add(0f)
    }

    // 그라디언트 색상 설정
    val gradientColors = if ((cprFeedback[0] == 2) && (cprFeedback[1] == 2) && (cprFeedback[2] == 2)) {
        listOf(wearColorPalette.secondary, Color.Black)
    } else {
        listOf(wearColorPalette.error, Color.Black)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = Brush.radialGradient(colors = gradientColors)) // 중앙에서 시작하는 원형 그라데이션
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom
        ) {
//            Text(
//                text = "$totalAcceleration",
//                fontSize = 18.sp,
//                color = Color.White, // 텍스트 색상
//                modifier = Modifier.padding(vertical = 24.dp),
//            )
//
//            Spacer(modifier = Modifier.height(32.dp))

            // 그래프 그리기 (투명 배경에 흰색 선 그래프)
            Canvas(modifier = Modifier.height(50.dp).fillMaxWidth()) {
                val maxData = max(accelerationData.maxOrNull() ?: 1f, 1f)
                val pointWidth = size.width / (accelerationData.size - 1)

                for (i in 0 until accelerationData.size - 1) {
                    val startX = i * pointWidth
                    val endX = (i + 1) * pointWidth

                    val startY = size.height - (accelerationData[i] / maxData) * size.height
                    val endY = size.height - (accelerationData[i + 1] / maxData) * size.height

                    drawLine(
                        color = Color.White, // 그래프 선 색상 흰색
                        start = Offset(startX, startY),
                        end = Offset(endX, endY),
                        strokeWidth = 4f,
                        cap = StrokeCap.Round
                    )
                }
            }

//            Spacer(modifier = Modifier.height(32.dp))

            Box(
                modifier = Modifier
                    .width(120.dp)
                    .clickable { onExitClick() }, // 클릭 이벤트 처리
                contentAlignment = Alignment.Center // 가운데 정렬 설정
            ) {
                Text(
                    text = "종료",
                    color = Color.White, // 텍스트 색상 흰색
                    modifier = Modifier.padding(top = 64.dp) // 텍스트 주변 패딩
                )
            }
        }
    }
}
