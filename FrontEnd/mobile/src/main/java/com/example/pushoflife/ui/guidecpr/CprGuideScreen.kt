package com.example.pushoflife.ui.guidecpr

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pushoflife.R
import com.example.pushoflife.ui.theme.PushOfLifeTheme
import androidx.compose.foundation.Image
import com.example.pushoflife.PretendardRegular
import com.example.pushoflife.PretendardSemiBold
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.pushoflife.ui.aedLocation.AedLocationScreen

val PretendardExtraBold = FontFamily(
    Font(R.font.pretendard_extrabold, FontWeight.ExtraBold)
)

val PretendardMedium = FontFamily(
    Font(R.font.pretendard_medium, FontWeight.Medium)
)

@Composable
fun CprGuideScreen(navController: NavController, modifier: Modifier = Modifier) {
    val pagerState = rememberPagerState(pageCount = { 5 }) // Pager 상태 관리
    val coroutineScope = rememberCoroutineScope() // CoroutineScope 사용

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically

        ) {
            // 뒤로 가기 버튼
            IconButton(
                onClick = { navController.popBackStack() }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.backicon),
                    contentDescription = "뒤로 가기",
                    modifier = Modifier
                        .size(25.dp) // 아이콘 크기
                )
            }

            // 타이틀 텍스트
            Text(
                text = "CPR 가이드",
                fontSize = 24.sp,  // 글자 크기
                textAlign = TextAlign.Center,
                fontFamily = PretendardSemiBold,
                letterSpacing = (-1).sp,
                modifier = Modifier
                    .weight(1f) // 남은 공간을 차지하도록 설정
                    .padding(end = 40.dp)
            )
        }

//        Spacer(modifier = Modifier.height(10.dp))

        // Pager
        HorizontalPager(
            state = pagerState, // Pager 상태 설정
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
        ) { page ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp)
                    .clip(RoundedCornerShape(12.dp))
//                    .background(color = when (page) {
//                        0 -> Color(0xFFE7CAD5)
//                        1 -> Color(0xFFDECBD3)
//                        2 -> Color(0xFFD0E1F9)
//                        3 -> Color(0xFFFFC1C1)
//                        else -> Color(0xFFD1E8E2)
//                    })
            ) {
                when (page) {
                    0 -> CardContent(
                        iconRes = R.drawable.number1,
                        imageRes = R.drawable.cpr_guide1, // 카드 1
                        topText = "반응 확인",
                        bottomText = "양쪽 어깨를 두드리며,\n환자의 의식과 반응 확인"
                    )
                    1 -> CardContent(
                        iconRes = R.drawable.number2,
                        imageRes = R.drawable.cpr_guide2, // 카드 2
                        topText = "신고 및 도움 요청",
                        bottomText = "119 신고 및 주변에\n자동심장충격기(AED) 요청"
                    )
                    2 -> CardContent(
                        iconRes = R.drawable.number3,
                        imageRes = R.drawable.cpr_guide3, // 카드 3
                        topText = "가슴 압박",
                        bottomText = "환자의 가슴 압박점을 찾아 깍지를 끼고,\n두 손의 손바닥 뒤꿈치로 압박 실시"
                        + "\n\n*분당 100~120회 속도, 약 5cm 깊이"
                    )
                    3 -> CardContent(
                        iconRes = R.drawable.number4,
                        imageRes = R.drawable.cpr_guide4, // 카드 4
                        topText = "호흡 확인",
                        bottomText = "환자의 얼굴과 가슴을\n10초 내로 관찰해 호흡 확인\n\n호흡이 없거나 비정상적이면\n즉시 심폐소생술 준비"
                    )
                    4 -> CardContent(
                        iconRes = R.drawable.number5,
                        imageRes = R.drawable.cpr_guide5, // 카드 5 이미지
                        topText = "인공호흡 2회 (권고 사항)",
                        bottomText = "환자의 머리를 뒤로 기울이고\n턱을 들어올려 기도를 유지,\n환자의 코를 막고 입을 환자 입에 밀착\n환자의 가슴이 올라올 정도로 1초 동안 숨 불어넣기"
                        + "\n\n*가슴 압박 : 인공호흡 = 30 : 2"
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 페이지 인디케이터
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(5) { index ->
                val color = if (pagerState.currentPage == index) {
                    Color(0xFFFF5A6A)
                } else {
                    Color.LightGray
                }
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(color = color, shape = RoundedCornerShape(100))
                )
                if (index < 4) {
                    Spacer(modifier = Modifier.width(8.dp)) // 인디케이터 간의 간격 설정 (마진처럼 동작)
                }
            }
        }

        Spacer(modifier = Modifier.height(25.dp))


        CardComponent1()

    }
}

@Composable
fun CardContent(iconRes: Int, imageRes: Int, topText: String, bottomText: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
    ) {
        // 상단 텍스트와 아이콘을 함께 배치하는 Row
        Row(
            verticalAlignment = Alignment.CenterVertically, // 텍스트와 아이콘 수직 중앙 정렬
            modifier = Modifier.fillMaxWidth()
        ) {
            // 좌측 아이콘
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = null, // 아이콘 설명 생략
                tint = Color.Unspecified,
                modifier = Modifier.size(30.dp) // 아이콘 크기
            )
            Spacer(modifier = Modifier.width(8.dp)) // 아이콘과 텍스트 사이 간격
            // 상단 텍스트
            Text(
                text = topText,
                fontSize = 23.sp,
                fontFamily = PretendardExtraBold,
                color = Color(0xFFFF5A6A),
                textAlign = TextAlign.Start,
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 하단 텍스트
        Text(
            text = bottomText,
            fontFamily = PretendardMedium,
            fontSize = 16.sp,
            letterSpacing = (-1).sp,
        )

        Spacer(modifier = Modifier.height(10.dp))

        Image(
            painter = painterResource(id = imageRes),
            contentDescription = bottomText,
            modifier = Modifier
                .fillMaxWidth()  // 가로로 최대 크기 차지
                .height(270.dp)  // 이미지의 높이를 명시적으로 설정
        )
    }
}

@Composable
fun CardComponent1() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(color = Color(0xFFE7CAD5))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(15.dp),
//            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 상단 텍스트
            Text(
                text = "CPR 압박 위치",
                fontSize = 20.sp,
                fontFamily = com.example.pushoflife.PretendardSemiBold,
                letterSpacing = (-0.5).sp,
                color = Color(0xFF3A3A3C),
                textAlign = TextAlign.Center,
            )

            // 중앙 이미지
            Image(
                painter = painterResource(id = R.drawable.card_cprwhere),
                contentDescription = "CPR 가이드",
                modifier = Modifier
                    .width(250.dp)
                    .height(150.dp)
            )

            // 하단 텍스트
            Text(
                text = "명치에 세 손가락을 얹고 \n 얼굴과 가장 가까운 손가락의 위치를 압박하세요!",
                fontSize = 15.sp,
                fontFamily = PretendardRegular,
                letterSpacing = (-0.5).sp,
                color = Color(0xFF3A3A3C),
                textAlign = TextAlign.Center,
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun CprGuideScreenPreview() {
    PushOfLifeTheme {
        val navController = rememberNavController()
        CprGuideScreen(navController = navController)
    }
}
