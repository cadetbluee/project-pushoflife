package com.example.pushoflife.ui.user

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pushoflife.MainScreenPreview
import com.example.pushoflife.R
import com.example.pushoflife.data.datastore.UserPreferences
import com.example.pushoflife.ui.theme.PushOfLifeTheme
import com.example.pushoflife.utils.GetLocation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.pushoflife.data.datastore.TokenPreferences
import com.example.pushoflife.network.LoginRequest
import com.example.pushoflife.network.RetrofitClient.apiService
import com.example.pushoflife.network.SignUpRequest
import kotlinx.coroutines.withContext

@Composable
fun LoginScreen(navController: NavController) {
    var user_phone by remember { mutableStateOf(TextFieldValue("")) } // 전화번호 상태
    var user_password by remember { mutableStateOf(TextFieldValue("")) } // 비밀번호 상태
    val context = LocalContext.current
    val tokenPreferences = TokenPreferences(context)
    val PretendardBold = FontFamily(
        Font(R.font.pretendard_semibold, FontWeight.SemiBold) // res/font/Poppins-ExtraLight.ttf 파일 참조
    )

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
                .padding(vertical = 16.dp), // 좌우 여백 추가
            verticalAlignment = Alignment.CenterVertically // 수직 가운데 정렬

        ) {
            // 뒤로 가기 버튼
            IconButton(
                onClick = { navController.popBackStack() }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.backicon),
                    contentDescription = "뒤로 가기"
                )
            }

            // 타이틀 텍스트
            Text(
                text = "로그인",
                fontSize = 24.sp,  // 글자 크기
                textAlign = TextAlign.Center,
                fontFamily = PretendardBold,
                modifier = Modifier
                    .weight(1f) // 남은 공간을 차지하도록 설정
                    .padding(end = 40.dp)

            )
        }
        Spacer(modifier = Modifier.height(64.dp))  // 간격 추가
        Text(
            text = "반가워요!",
            fontSize = 28.sp,  // 글자 크기
            textAlign = TextAlign.Left,
            fontFamily = PretendardBold,

            )
        Text(
            text = "로그인을 도와드릴게요",
            fontSize = 28.sp,  // 글자 크기
            textAlign = TextAlign.Left,
            fontFamily = PretendardBold,


            )
        Spacer(modifier = Modifier.height(96.dp))  // 간격 추가


        // 전화번호 입력 필드 (아이콘 포함)
        TextField(
            value = user_phone,
            onValueChange = { user_phone = it },
            label = { Text(text = "전화번호") },  // "전화번호" 레이블
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.phoneicon),
                    contentDescription = "Phone Icon"
                )
            },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors( unfocusedContainerColor = MaterialTheme.colorScheme.background )
        )

        Spacer(modifier = Modifier.height(8.dp))  // 간격 추가
        // 비밀번호 입력 필드 (아이콘 포함)
        TextField(
            value = user_password,
            onValueChange = { user_password = it },
            label = { Text(text = "비밀번호") },  // "비밀번호" 레이블
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.passwordicon),
                    contentDescription = "Password Icon"
                )
            },
            visualTransformation =  PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors( unfocusedContainerColor = MaterialTheme.colorScheme.background )
        )

        Spacer(modifier = Modifier.height(64.dp))  // 간격 추가

        // 저장 버튼
        Button(
            onClick = {
                CoroutineScope(Dispatchers.IO).launch {
                    val loginRequest = LoginRequest(
                        user_password = user_password.text,
                        user_phone = user_phone.text,
                    )

                    // API 요청 보내기
                    val response = apiService.loginUser(loginRequest).execute()
                    if (response.isSuccessful) {
                        // Authorization 헤더에서 토큰 추출
                        val authToken = response.headers()["Authorization"]

                        // authToken이 null이 아닌 경우 DataStore에 저장
                        authToken?.let { token ->
                            tokenPreferences.saveAuthToken(token)
                        }
                        withContext(Dispatchers.Main) {
                            navController.navigate("main_screen") // navigate 호출을 메인 스레드에서 실행
                        }
                    } else {
                        // 로그인 실패 처리
                        Log.d("Login failed: ", response.message())
                    }
                }

            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "로그인")
        }
        Spacer(modifier = Modifier.height(5.dp))  // 간격 추가
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center

        ){
            Text(
                text = "아직 회원이 아니신가요?",
                fontSize = 16.sp,  // 글자 크기
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 4.dp)
            )
            Text(
                text = "회원가입",
                fontSize = 16.sp,  // 글자 크기
                textAlign = TextAlign.Center,
                fontFamily = PretendardBold,
                modifier = Modifier

                    .clickable {
                        // 로그인 페이지로 이동
                        navController.navigate("sign_up_screen")
                    }
            )
        }

    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    PushOfLifeTheme {
        val navController = rememberNavController() // 가짜 NavController 생성
        LoginScreen(navController = navController) // 가짜 NavController 전달
    }
}
