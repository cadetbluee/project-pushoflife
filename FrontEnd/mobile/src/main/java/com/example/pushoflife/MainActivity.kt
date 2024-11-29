package com.example.pushoflife

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.pushoflife.ui.serviceguide.ServiceScreen
import com.example.pushoflife.ui.user.ProfileEditScreen
import com.example.pushoflife.ui.theme.PushOfLifeTheme
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.example.pushoflife.R

// 폰트 정의
val PoppinsExtraLight = FontFamily(
    Font(R.font.poppins_extralight, FontWeight.ExtraLight) // res/font/Poppins-ExtraLight.ttf 파일 참조
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PushOfLifeTheme {
                val navController = rememberNavController() // Navigation Controller 생성
                NavHost(navController = navController, startDestination = "main_screen") {
                    composable("main_screen") { MainScreen(navController) }  // 메인 화면
                    composable("service_screen") { ServiceScreen() }          // 서비스 가이드 화면
                    composable("profile_edit_screen") { ProfileEditScreen() } // 프로필 수정 화면
                }
            }
        }
    }
}

@Composable
fun MainScreen(navController: NavHostController, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 상단의 'Push of Life' 텍스트와 이미지
        Row(
            verticalAlignment = Alignment.CenterVertically,  // 수직으로 가운데 정렬
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {

        Text(
            text = stringResource(id = R.string.main_title),
            fontFamily = PoppinsExtraLight,
            fontSize = 32.sp,
            textAlign = TextAlign.Start,
            modifier = Modifier.weight(1f)
        )

            // 이미지 클릭 시 프로필 수정 화면으로 이동
            Image(
                painter = painterResource(id = R.drawable.profileicon),  // VectorDrawable 사용
                contentDescription = "Profile Icon",
                modifier = Modifier
                    .size(50.dp)  // 이미지 크기 설정
                    .clickable { navController.navigate("profile_edit_screen") },  // 클릭 이벤트 처리
                contentScale = ContentScale.Fit
            )
        }

        Spacer(modifier = Modifier.height(50.dp)) // 원하는 만큼 높이를 설정

        // '서비스 가이드' 버튼
        Card(
            onClick = { navController.navigate("service_screen") },
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
        ) {
            Text(text = "서비스 가이드", fontSize = 20.sp)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    PushOfLifeTheme {
        MainScreen(navController = rememberNavController())
    }
}
