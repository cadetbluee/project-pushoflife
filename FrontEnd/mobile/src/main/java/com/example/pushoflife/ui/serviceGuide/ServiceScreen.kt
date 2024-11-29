package com.example.pushoflife.ui.serviceguide

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.pushoflife.ui.theme.PushOfLifeTheme
import com.example.pushoflife.R

val PretendardBold = FontFamily(
    Font(R.font.pretendard_semibold, FontWeight.SemiBold)
)

val PretendardMedium = FontFamily(
    Font(R.font.pretendard_medium, FontWeight.Medium)
)

@Composable
fun ServiceScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 5.dp)
    ) {
        // 상단 타이틀과 뒤로가기 버튼 Row (고정)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 뒤로 가기 버튼
            IconButton(
                onClick = { navController.popBackStack() }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.backicon),
                    contentDescription = "뒤로 가기",
                    modifier = Modifier.size(25.dp)
                )
            }

            // 타이틀 텍스트
            Text(
                text = stringResource(id = R.string.service_screen_text),
                fontSize = 24.sp,
                textAlign = TextAlign.Center,
                fontFamily = PretendardBold,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 40.dp)
            )
        }

        // 스크롤 가능한 가이드 리스트
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                GuideBox(
                    numberIconId = R.drawable.number1,
                    title = "워치에서 어플 실행",
                    description = "응급환자 발견 시 메인을 터치해 주세요\n3초 동안 동작이 없으면 응급신고가 들어가고\nCPR 피드백이 시작돼요",
                    imageId = R.drawable.app_guide_1
                )
            }

            item {
                GuideBox(
                    numberIconId = R.drawable.number2,
                    title = "가이드에 맞춰 CPR 수행",
                    description = "소리에 맞춰 CPR을 수행해 주세요\n빈도, 각도, 깊이에 대한 피드백을 제공해요",
                    imageId = R.drawable.app_guide_2
                )
            }

            item {
                GuideBox(
                    numberIconId = R.drawable.number3,
                    title = "AED 위치 제공",
                    description = "현재 위치 주변의 AED 위치와 정보를 확인할 수 있어요",
                    imageId = R.drawable.app_guide_3
                )
                Spacer(modifier = Modifier.height(35.dp))
            }
        }
    }
}

@Composable
fun GuideBox(
    numberIconId: Int,
    title: String,
    description: String,
    imageId: Int
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            Icon(
                painter = painterResource(id = numberIconId),
                tint = Color(0xFFFF5A6A),
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = title,
                fontSize = 20.sp,
                fontFamily = PretendardBold,
                letterSpacing = (-0.5).sp,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Text(
            text = description,
            fontSize = 16.sp,
            fontFamily = PretendardMedium,
            textAlign = TextAlign.Start,
            letterSpacing = (-0.5).sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )

        Image(
            painter = painterResource(id = imageId),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .aspectRatio(16 / 9f)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ServiceScreenPreview() {
    PushOfLifeTheme {
        val navController = rememberNavController()
        ServiceScreen(navController = navController)
    }
}
