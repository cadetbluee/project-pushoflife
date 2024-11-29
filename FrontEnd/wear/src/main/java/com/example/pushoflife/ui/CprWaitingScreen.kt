package com.example.pushoflife.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import com.example.pushoflife.network.MessageSender
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.pushoflife.theme.wearColorPalette
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.pushoflife.R

val PretendardRegular = FontFamily(
    Font(R.font.pretendard_regular, FontWeight.Normal)
)

@Composable
fun CprWaitingScreen(
    nodeId: String?,
    messageSender: MessageSender,
    onNavigateToCprGuide: () -> Unit
) {
    var countdown by remember { mutableStateOf(3) } // 3초부터 시작
    val coroutineScope = rememberCoroutineScope()
    val gradientColors = listOf(wearColorPalette.error, Color.Black)

    // 카운트다운을 위한 LaunchedEffect
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            while (countdown > 0) {
                delay(1000L) // 1초 지연
                countdown -= 1
            }
            // 카운트다운이 끝나면 자동 신고 로직 실행
            if (countdown == 0) {
                messageSender.sendEmergencyCall(nodeId)
            }
        }
    }

    // UI 구성
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.radialGradient(gradientColors))
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 카운트다운 텍스트 표시
        Text(
            text = "$countdown",
            fontSize = 35.sp,
            color = Color.White, // 텍스트 색상 흰색으로 설정
            fontFamily = PretendardSemiBold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // 안내 문구 표시
        Text(
            text = "119 응급신고를 할까요?",
            fontSize = 20.sp,
            color = Color.White, // 텍스트 색상 흰색으로 설정
            fontFamily = PretendardSemiBold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // 자동 신고 안내
        Text(
            text = "응답이 없을 시 3초 후 자동 신고됩니다.",
            fontSize = 12.sp,
            color = Color.White, // 텍스트 색상 흰색으로 설정
            fontFamily = PretendardRegular,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // 버튼 섹션
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            // X 버튼: 빨간색 원형 버튼으로 설정
            Box(
                modifier = Modifier
                    .size(48.dp) // 아이콘 버튼 크기 줄임
                    .background(wearColorPalette.secondaryVariant, shape = CircleShape) // 완전 둥근 모양과 빨간색 배경
                    .clickable { onNavigateToCprGuide() }, // 클릭 이벤트 처리
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "취소",
//                    tint = Color.White // 아이콘 색상 흰색으로 설정
                )
            }
        }
    }
}