package com.example.pushoflife.ui.user

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
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
import com.example.pushoflife.BuildConfig
import com.example.pushoflife.utils.TwilioUtils
import java.util.Calendar
import android.app.DatePickerDialog
import android.util.Log
import androidx.compose.ui.viewinterop.AndroidView
import java.text.SimpleDateFormat
import java.util.Locale
import android.widget.DatePicker
import com.example.pushoflife.data.datastore.TokenPreferences
import com.example.pushoflife.network.RetrofitClient.apiService
import com.example.pushoflife.network.UpdateUserRequest
import com.google.android.gms.wearable.Wearable
import com.google.android.gms.wearable.PutDataMapRequest
import kotlinx.coroutines.withContext
import retrofit2.HttpException

@Composable
fun ProfileEditScreen(navController: NavController) {
    var user_name by remember { mutableStateOf(TextFieldValue("")) }  // 사용자 이름 상태
    var user_birthdate by remember { mutableStateOf(TextFieldValue("")) } // 생년월일 상태
    var user_gender by remember { mutableStateOf(TextFieldValue("")) } // 성별 상태
    var expandedGender by remember { mutableStateOf(false) } // 성별 메뉴 확장 상태
    var user_disease by remember { mutableStateOf(TextFieldValue("")) } // 질환 상태
    var hospital by remember { mutableStateOf(TextFieldValue("")) } // 병원 상태
    var pill by remember { mutableStateOf(TextFieldValue("")) } // 복용 약 상태
    var user_protector by remember { mutableStateOf(TextFieldValue("")) } // 전화번호 상태
    var user_address by remember { mutableStateOf(TextFieldValue("")) } // 자택주소 상태
    val PretendardBold = FontFamily(
        Font(R.font.pretendard_semibold, FontWeight.SemiBold) // res/font/Poppins-ExtraLight.ttf 파일 참조
    )
    val PretendardRegular = FontFamily(
        Font(R.font.pretendard_regular, FontWeight.Normal)
    )

    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    var showDatePicker by remember { mutableStateOf(false) }


    val address = GetLocation(context)
    val tokenPreferences = TokenPreferences(context)
    val userPreferences = UserPreferences(context)
    var showModal by remember { mutableStateOf(false) }
    var isLoggedIn by remember { mutableStateOf(false) }
    var authToken by remember { mutableStateOf<String?>(null) }
    val messageContent = buildString {
        if (user_name.text.isNotEmpty()) append("${user_name.text}님의 비상 연락처입니다.\n")
        else append("비상 연락처입니다.\n")
        append("환자가 쓰러져 자동 신고되었습니다.\n")
        append("환자 위치: $address")
    }

    // LaunchedEffect를 사용하여 초기화 시 DataStore에서 데이터를 불러옴
    LaunchedEffect(Unit) {
        userPreferences.getUserProfile().collect { userProfile ->
            user_name = TextFieldValue(userProfile.name)
            user_birthdate = TextFieldValue(userProfile.birthdate)
            user_gender = TextFieldValue(userProfile.gender)
            user_disease = TextFieldValue(userProfile.disease)
            hospital = TextFieldValue(userProfile.hospital)
            pill = TextFieldValue(userProfile.pill)
            user_protector = TextFieldValue(userProfile.protector)
            user_address = TextFieldValue(userProfile.address)
        }
    }

    // 토큰이 있는지 확인하고, 있으면 서버에서 데이터 불러오기
    LaunchedEffect(Unit) {
        tokenPreferences.getAuthToken().collect { token ->
            authToken = token
            isLoggedIn = !token.isNullOrEmpty()

            if (isLoggedIn && authToken != null) {
                // 서버에서 유저 정보 가져오기
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val response = apiService.getUserInfo("$authToken").execute()
                        if (response.isSuccessful) {
                            response.body()?.let { userInfo ->
                                // 서버에서 가져온 데이터를 DataStore에 저장
                                userPreferences.saveUserProfile(
                                    name = userInfo.user_name ?: "",
                                    birthdate = userInfo.user_birthday ?: "",
                                    gender = userInfo.user_gender ?: "",
                                    disease = userInfo.user_disease ?: "",
                                    protector = userInfo.user_protector ?: "",
                                    address = user_address.text,
                                    hospital = hospital.text,
                                    pill = pill.text
                                )
                            }
                        } else {
                            println("Failed to fetch user info: ${response.errorBody()?.string()}")
                            println("Failed to fetch user info: ${authToken}")
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        println("Error fetching user info: ${e.message}")
                    }
                }
            }
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
                text = "정보 추가",
                fontSize = 24.sp,  // 글자 크기
                textAlign = TextAlign.Center,
                fontFamily = PretendardBold,
                modifier = Modifier
                    .weight(1f) // 남은 공간을 차지하도록 설정
                    .padding(end = 40.dp)

            )
        }
        Spacer(modifier = Modifier.height(16.dp))  // 간격 추가

        Text(
            text = "위급상황 시 아래 정보를 구급대에 같이 전달합니다.",
            fontSize = 14.sp,
            textAlign = TextAlign.Left,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(4.dp))  // 간격 추가
        // 이름 입력 필드 (아이콘 포함)
        TextField(
            value = user_name,
            onValueChange = { user_name = it },
            label = { Text(text = "이름") },  // "이름" 레이블
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.peopleicon),
                    contentDescription = "Name Icon"
                )
            },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors( unfocusedContainerColor = MaterialTheme.colorScheme.background )
        )

        Spacer(modifier = Modifier.height(8.dp))  // 간격 추가

        // 생년월일 입력 필드 (DatePicker)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showDatePicker = true }
        ) {
            TextField(
                value = user_birthdate,
                onValueChange = { user_birthdate = it },
                label = { Text(text = "생년월일") },
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.dateicon),
                        contentDescription = "Birthdate Icon"
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = MaterialTheme.colorScheme.background,
                    disabledTextColor = MaterialTheme.colorScheme.primary,
                    disabledBorderColor=MaterialTheme.colorScheme.primary,
                    disabledLeadingIconColor = MaterialTheme.colorScheme.primary,
                    disabledLabelColor = MaterialTheme.colorScheme.primary,
                ),
                enabled = false
            )
        }

        Spacer(modifier = Modifier.height(8.dp))  // 간격 추가

        // 성별 선택 드롭다운 메뉴
        Box(modifier = Modifier.fillMaxWidth()) {
            TextField(
                value = user_gender,
                onValueChange = { },
                label = { Text(text = "성별") },
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.gendericon),
                        contentDescription = "Gender Icon"
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expandedGender = true },
                readOnly = true,
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = MaterialTheme.colorScheme.background,
                    disabledTextColor = MaterialTheme.colorScheme.primary,
                    disabledBorderColor=MaterialTheme.colorScheme.primary,
                    disabledLeadingIconColor = MaterialTheme.colorScheme.primary,
                    disabledLabelColor = MaterialTheme.colorScheme.primary,
                ),
                enabled = false
            )

            DropdownMenu(
                expanded = expandedGender,
                onDismissRequest = { expandedGender = false },
                modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.background) // 배경색 흰색으로 설정

            ) {

                DropdownMenuItem(onClick = {
                    user_gender = TextFieldValue("여") // TextFieldValue로 감싸기
                    expandedGender = false
                }, text = { Text("여") })
                DropdownMenuItem(onClick = {
                    user_gender = TextFieldValue("남") // TextFieldValue로 감싸기
                    expandedGender = false
                }, text = { Text("남") })
            }
        }

        Spacer(modifier = Modifier.height(8.dp))  // 간격 추가

        // 질환 입력 필드 (아이콘 포함)
        TextField(
            value = user_disease,
            onValueChange = { user_disease = it },
            label = { Text(text = "기저질환") },  // "질환" 레이블
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.bandageicon),
                    contentDescription = "Disease Icon"
                )
            },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors( unfocusedContainerColor = MaterialTheme.colorScheme.background )
        )

        Spacer(modifier = Modifier.height(8.dp))  // 간격 추가

        // 병원 입력 필드 (아이콘 포함)
        TextField(
            value = hospital,
            onValueChange = { hospital = it },
            label = { Text(text = "병원") },  // "병원" 레이블
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.hospitalicon),
                    contentDescription = "Hospital Icon"
                )
            },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors( unfocusedContainerColor = MaterialTheme.colorScheme.background )
        )

        Spacer(modifier = Modifier.height(8.dp))  // 간격 추가

        // 복용 약 입력 필드 (아이콘 포함)
        TextField(
            value = pill,
            onValueChange = { pill = it },
            label = { Text(text = "복용중인 약") },  // "복용 약" 레이블
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.pillicon),
                    contentDescription = "Pill Icon"
                )
            },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors( unfocusedContainerColor = MaterialTheme.colorScheme.background )
        )

        Spacer(modifier = Modifier.height(8.dp))  // 간격 추가

        // 전화번호 입력 필드 (아이콘 포함)
        TextField(
            value = user_protector,
            onValueChange = { user_protector = it },
            label = { Text(text = "비상 연락처") },  // "전화번호" 레이블
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

        // 전화번호 입력 필드 (아이콘 포함)
        TextField(
            value = user_address,
            onValueChange = { user_address = it },
            label = { Text(text = "자택 주소" )},  // "자택 주소" 레이블
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.homeicon),
                    contentDescription = "Address Icon"
                )
            },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors( unfocusedContainerColor = MaterialTheme.colorScheme.background )
        )

        Spacer(modifier = Modifier.height(32.dp))  // 간격 추가

        // 저장 버튼
        Button(
            onClick = {
                showModal = true

            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "저장하기")
        }
        Spacer(modifier = Modifier.height(5.dp))  // 간격 추가
        if (isLoggedIn) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center

            ) {
                Text(
                    text = "로그아웃",
                    fontSize = 16.sp,  // 글자 크기
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .clickable {
                            // 로그아웃 기능
                            CoroutineScope(Dispatchers.IO).launch {
                                tokenPreferences.clearAuthToken()
                                authToken = null
                                isLoggedIn = false
                            }
                        }
                )
            }
        }else {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center

            ) {
                Text(
                    text = "로그아웃해도 정보를 기억할래요!",
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
        // 모달(다이얼로그) 표시
        if (showDatePicker) {
            AlertDialog(
                onDismissRequest = { showDatePicker = false },
                title = {
                    Text(
                        text = "날짜 선택",
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 20.sp,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                },
                text = {
                    AndroidView(
                        modifier = Modifier.fillMaxWidth(),
                        factory = { context ->
                            DatePicker(context).apply {
                                init(
                                    calendar.get(Calendar.YEAR),
                                    calendar.get(Calendar.MONTH),
                                    calendar.get(Calendar.DAY_OF_MONTH)
                                ) { _, year, month, dayOfMonth ->
                                    calendar.set(year, month, dayOfMonth)
                                }
                            }
                        }
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            val formattedDate = SimpleDateFormat("yyyy.MM.dd", Locale.KOREA).format(calendar.time)
                            user_birthdate = TextFieldValue(formattedDate)
                            showDatePicker = false
                        }
                    ) {
                        Text(text = "확인", color = MaterialTheme.colorScheme.primary) // 보라색 확인 버튼
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showDatePicker = false }
                    ) {
                        Text(text = "취소", color =  MaterialTheme.colorScheme.primary) // 회색 취소 버튼
                    }
                },
                containerColor =  MaterialTheme.colorScheme.background // 다이얼로그 배경 흰색
            )
        }

        if (showModal) {

            AlertDialog(
                containerColor= MaterialTheme.colorScheme.background,
                onDismissRequest = { showModal = false },
                title = {
                    Text(
                        text = "해당 내용을 저장하시겠습니까?",
                        fontSize = 18.sp

                    )
                },
                text = {
                    // Column으로 Text와 Box를 함께 배치
                    Column {
                        Text(
                            text = "119 신고 문자 메시지 예시",

                        )
                        Spacer(modifier = Modifier.height(5.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.tertiary), // Box의 배경색을 파란색으로 설정
                            contentAlignment = Alignment.CenterStart // Box의 중앙이지만 왼쪽 정렬
                            ) {
                            Column(

                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(text = "[WEB 발신]", fontSize = 14.sp, color = MaterialTheme.colorScheme.background)
                                Text(text = "심정지 환자 발생", fontSize = 14.sp, color = MaterialTheme.colorScheme.background)
                                Text(text = "위치: ${address}", fontSize = 14.sp, color = MaterialTheme.colorScheme.background)
                                Text(text = "인적사항", fontSize = 14.sp, color = MaterialTheme.colorScheme.background)
                                if (user_name.text.isNotEmpty()) {Text(text = "- 이름: ${user_name.text}", fontSize = 14.sp, color = MaterialTheme.colorScheme.background)}
                                if (user_birthdate.text.isNotEmpty()) {Text(text = "- 생년월일: ${user_birthdate.text}", fontSize = 14.sp, color = MaterialTheme.colorScheme.background)}
                                if (user_gender.text.isNotEmpty()) {Text(text = "- 성별: ${user_gender.text}", fontSize = 14.sp, color = MaterialTheme.colorScheme.background)}
                                if (user_disease.text.isNotEmpty()) {Text(text = "기저질환: ${user_disease.text}", fontSize = 14.sp, color = MaterialTheme.colorScheme.background)}
                                if (hospital.text.isNotEmpty()) {Text(text = "자주 가는 병원: ${hospital.text}", fontSize = 14.sp, color = MaterialTheme.colorScheme.background,)}
                                if (pill.text.isNotEmpty()) {Text(text = "복용 약: ${pill.text}", fontSize = 14.sp, color = MaterialTheme.colorScheme.background)}
                                if (user_protector.text.isNotEmpty()) {Text(text = "비상 연락처: ${user_protector.text}", fontSize = 14.sp, color = MaterialTheme.colorScheme.background)}
                                if (user_address.text.isNotEmpty()) {Text(text = "자택 주소: ${user_address.text}", fontSize = 14.sp, color = MaterialTheme.colorScheme.background)}
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
//                            TwilioUtils.sendSmsWithRetrofit(
//                                "",
//                                BuildConfig.FROM_NUMBER,
//                                messageContent,
//                                BuildConfig.TWILIO_ACCOUNT_SID,
//                                BuildConfig.TWILIO_AUTH_TOKEN
//                            )
                            if (isLoggedIn && authToken != null) {
                                // AccessToken이 존재하는 경우 서버 업데이트 요청
                                CoroutineScope(Dispatchers.IO).launch {
                                    try {
                                        val updateUserRequest = UpdateUserRequest(
                                            user_name = if (user_name.text.isNotEmpty()) user_name.text else null,
                                            user_birthday = if(user_birthdate.text.isNotEmpty())user_birthdate.text else null,
                                            user_gender = if (user_gender.text.isNotEmpty()) user_gender.text else null,
                                            user_disease = if (user_disease.text.isNotEmpty()) user_disease.text else null,
                                            user_protector = if (user_protector.text.isNotEmpty()) user_protector.text else null,
                                        )

                                        // API 요청 보내기
                                        val response = apiService.updateUser(
                                            token = "$authToken",
                                            updateUserRequest = updateUserRequest
                                        ).execute()

                                        if (response.isSuccessful) {
                                            // 서버 업데이트 성공 처리
                                            response.body()?.let { userInfo ->
                                                // 서버에서 가져온 데이터를 DataStore에 저장
                                                userPreferences.saveUserProfile(
                                                    name = userInfo.user_name ?: "",
                                                    birthdate = userInfo.user_birthday ?: "",
                                                    gender = userInfo.user_gender ?: "",
                                                    disease = userInfo.user_disease ?: "",
                                                    protector = userInfo.user_protector ?: "",
                                                    hospital = hospital.text,
                                                    pill= pill.text,
                                                    address=user_address.text
                                                )
                                            }
                                            println("Profile updated successfully!")
                                            withContext(Dispatchers.Main) {
                                                navController.navigate("main_screen") // navigate 호출을 메인 스레드에서 실행
                                            }
                                        } else {
                                            // 실패 처리
                                            println("Update failed: ${response.errorBody()?.string()}")
                                        }
                                    }catch (e: HttpException) {
                                    println("Exception during profile update: ${e.message}")
                                }
                                }
                            } else {
                                // AccessToken이 없는 경우 데이터 로컬 저장
                                CoroutineScope(Dispatchers.IO).launch {
                                    userPreferences.saveUserProfile(
                                        name = user_name.text,
                                        birthdate = user_birthdate.text,
                                        gender = user_gender.text,
                                        disease = user_disease.text,
                                        protector = user_protector.text,
                                        hospital = hospital.text,
                                        pill= pill.text,
                                        address=user_address.text
                                    )
                                    println("Local profile updated")
                                    withContext(Dispatchers.Main) {
                                        navController.navigate("main_screen") // navigate 호출을 메인 스레드에서 실행
                                    }
                                }
                            }
                            // 데이터를 Wear OS로 전송하는 로직 추가
                            CoroutineScope(Dispatchers.IO).launch {
                                val putDataMapRequest = PutDataMapRequest.create("/notification_path")
                                putDataMapRequest.dataMap.putString("name", user_name.text)
                                putDataMapRequest.dataMap.putString("birthdate", user_birthdate.text)
                                putDataMapRequest.dataMap.putString("gender", user_gender.text)
                                putDataMapRequest.dataMap.putString("disease", user_disease.text)
                                putDataMapRequest.dataMap.putString("hospital", hospital.text)
                                putDataMapRequest.dataMap.putString("pill", pill.text)
                                putDataMapRequest.dataMap.putString("protector", user_protector.text)

                                val putDataRequest = putDataMapRequest.asPutDataRequest()
                                putDataRequest.setUrgent()

                                Wearable.getDataClient(context).putDataItem(putDataRequest)
                                    .addOnSuccessListener {
                                        Log.d("Wearable", "Data sent successfully")
                                    }
                                    .addOnFailureListener {
                                        Log.e("Wearable", "Failed to send data", it)
                                    }
                            }
                        },
                    ) {
                        Text(
                            text = "저장"
                        )
                    }
                }
            )
        }

    }
}

@Preview(showBackground = true)
@Composable
fun ProfileEditScreenPreview() {
    PushOfLifeTheme {
        val navController = rememberNavController() // 가짜 NavController 생성
        ProfileEditScreen(navController = navController) // 가짜 NavController 전달
    }
}
