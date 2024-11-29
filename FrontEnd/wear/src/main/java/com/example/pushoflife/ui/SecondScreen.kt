package com.example.pushoflife.ui

import android.content.Intent
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pushoflife.MainActivity
import com.example.pushoflife.R
import androidx.compose.ui.platform.LocalContext
import androidx.wear.compose.material.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.example.pushoflife.network.MessageSender
import kotlinx.coroutines.delay

val PretendardSemiBold = FontFamily(
    Font(R.font.pretendard_semibold, FontWeight.SemiBold)
)

@Composable
fun SecondScreen(
    nodeId: String?,
    messageSender: MessageSender,
    onNavigateToCprGuide: () -> Unit) {
    val context = LocalContext.current
    var isVisible by remember { mutableStateOf(true) }

    // 깜빡거림을 구현하는 LaunchedEffect
    LaunchedEffect(Unit) {
        while (true) {
            isVisible = !isVisible
            delay(500) // 0.5초마다 깜빡임
        }
    }
    //Log.d("nodeID","$nodeId")
    // 배경색과 텍스트/아이콘 색상을 조건부로 설정
    val backgroundColor = if (isVisible) Color.White else Color.Black
    val textColor = if (isVisible) Color.Black else Color.White

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(top = 30.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 깜빡이는 텍스트 박스
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    //Log.d("MessageSender","$nodeId")
                    nodeId?.let {
                        messageSender.sendCPRStart(
                            nodeId = it,
                            path = "/CPR_start",
                            message = "CPR시작"
                        )
                        //Log.d("메세지 보냄","$messageSender")
                    }
                    onNavigateToCprGuide() },

            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "CPR을\n시행하려면\n터치",
                fontSize = 35.sp,
                fontFamily = PretendardSemiBold,
                color = textColor,
                textAlign = TextAlign.Center
            )
        }

        // 깜빡이는 하단 이미지 버튼
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.quit_button),
                contentDescription = "Quit",
                modifier = Modifier
                    .size(50.dp)
                    .clickable {
                        val intent = Intent(context, MainActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                        }
                        context.startActivity(intent)
                    },
                alpha = if (isVisible) 1f else 0.5f // 아이콘도 투명도 조절 가능
            )
        }
    }
}