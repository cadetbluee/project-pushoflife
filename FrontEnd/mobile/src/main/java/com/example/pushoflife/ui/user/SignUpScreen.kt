package com.example.pushoflife.ui.user

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
fun SignUpScreen(navController: NavController) {
    var user_phone by remember { mutableStateOf(TextFieldValue("")) } // 전화번호 상태
    var user_password1 by remember { mutableStateOf(TextFieldValue("")) } // 비밀번호1 상태
    var user_password2 by remember { mutableStateOf(TextFieldValue("")) } // 비밀번호2 상태
    val PretendardBold = FontFamily(
        Font(R.font.pretendard_semibold, FontWeight.SemiBold) // res/font/Poppins-ExtraLight.ttf 파일 참조
    )
    // Optional fields with initial empty values
    var user_name by remember { mutableStateOf(TextFieldValue("")) }
    var user_birthdate by remember { mutableStateOf(TextFieldValue("")) }
    var user_gender by remember { mutableStateOf(TextFieldValue("")) }
    var user_disease by remember { mutableStateOf(TextFieldValue("")) }
    var user_protector by remember { mutableStateOf(TextFieldValue("")) }
    val context = LocalContext.current
    val tokenPreferences = TokenPreferences(context)
    val userPreferences = UserPreferences(context)
    // 데이터 불러오기
    LaunchedEffect(Unit) {
        userPreferences.getUserProfile().collect { userProfile ->
            user_name = TextFieldValue(userProfile.name ?: "")
            user_birthdate = TextFieldValue(userProfile.birthdate ?: "")
            user_gender = TextFieldValue(userProfile.gender ?: "")
            user_disease = TextFieldValue(userProfile.disease ?: "")
            user_protector = TextFieldValue(userProfile.protector ?: "")
        }
    }

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
                    contentDescription = "뒤로 가기",
                    modifier = Modifier
                    .size(25.dp) // 아이콘 크기
                )
            }

            // 타이틀 텍스트
            Text(
                text = "회원가입",
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
            text = "환영합니다!",
            fontSize = 28.sp,  // 글자 크기
            textAlign = TextAlign.Left,
            fontFamily = PretendardBold,



        )
        Text(
            text = "회원가입을 도와드릴게요",
            fontSize = 28.sp,  // 글자 크기
            textAlign = TextAlign.Left,
            fontFamily = PretendardBold,


        )
        Spacer(modifier = Modifier.height(32.dp))  // 간격 추가

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
            value = user_password1,
            onValueChange = { user_password1 = it },
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
        Spacer(modifier = Modifier.height(8.dp))  // 간격 추가
        // 비밀번호 확인 입력 필드 (아이콘 포함)
        TextField(
            value = user_password2,
            onValueChange = { user_password2 = it },
            label = { Text(text = "비밀번호 확인") },  // "비밀번호 확인" 레이블
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
                    val signUpRequest = SignUpRequest(
                        user_name = if (user_name.text.isNotEmpty()) user_name.text else null,
                        user_password1 = user_password1.text,
                        user_password2 = user_password2.text,
                        user_birthday = if(user_birthdate.text.isNotEmpty())user_birthdate.text else null,
                        user_gender = if (user_gender.text.isNotEmpty()) user_gender.text else null,
                        user_disease = if (user_disease.text.isNotEmpty()) user_disease.text else null,
                        user_phone = user_phone.text,
                        user_protector = if (user_protector.text.isNotEmpty()) user_protector.text else null
                    )

                    // 회원가입 API 요청 보내기
                    val signUpResponse = apiService.signUpUser(signUpRequest).execute()
                    if (signUpResponse.isSuccessful) {
                        // 회원가입 성공 시 로그인 API 요청
                        val loginRequest = LoginRequest(
                            user_phone = user_phone.text,
                            user_password = user_password1.text
                        )
                        val loginResponse = apiService.loginUser(loginRequest).execute()
                        if (loginResponse.isSuccessful) {
                            // Authorization 헤더에서 토큰 추출
                            val authToken = loginResponse.headers()["Authorization"]

                            // authToken이 null이 아닌 경우 DataStore에 저장
                            authToken?.let { token ->
                                tokenPreferences.saveAuthToken(token)
                            }
                            withContext(Dispatchers.Main) {
                                navController.navigate("main_screen") // navigate 호출을 메인 스레드에서 실행
                            }
                        } else {
                            // 로그인 실패 처리
                            println("Login failed: ${loginResponse.message()}")
                        }
                    } else {
                        // 회원가입 실패 처리
                        println("Sign up failed: ${signUpResponse.message()}")
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "회원가입")
        }
        Spacer(modifier = Modifier.height(5.dp))  // 간격 추가
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center

        ){
            Text(
                text = "이미 회원이신가요?",
                fontSize = 16.sp,  // 글자 크기
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 4.dp)
            )
            Text(
                    text = "로그인",
            fontSize = 16.sp,  // 글자 크기
            textAlign = TextAlign.Center,
                fontFamily = PretendardBold,
            modifier = Modifier

                .clickable {
                    // 로그인 페이지로 이동
                    navController.navigate("login_screen")
                }
            )
        }


    }
}

@Preview(showBackground = true)
@Composable
fun SignUpScreenPreview() {
    PushOfLifeTheme {
        val navController = rememberNavController() // 가짜 NavController 생성
        SignUpScreen(navController = navController) // 가짜 NavController 전달
    }
}
