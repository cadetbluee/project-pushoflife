package com.example.pushoflife.ui.user

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
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
import com.example.pushoflife.R
import com.example.pushoflife.ui.theme.PushOfLifeTheme

@Composable
fun ProfileEditScreen() {
    var user_name by remember { mutableStateOf(TextFieldValue("")) }  // 사용자 이름 상태
    var user_birthdate by remember { mutableStateOf(TextFieldValue("")) } // 생년월일 상태
    var user_gender by remember { mutableStateOf(TextFieldValue("")) } // 성별 상태
    var user_disease by remember { mutableStateOf(TextFieldValue("")) } // 질환 상태
    var hospital by remember { mutableStateOf(TextFieldValue("")) } // 병원 상태
    var pill by remember { mutableStateOf(TextFieldValue("")) } // 복용 약 상태
    var user_protector by remember { mutableStateOf(TextFieldValue("")) } // 전화번호 상태
    val PretendardRegualr = FontFamily(
        Font(R.font.pretendard_regular, FontWeight.Normal) // res/font/Poppins-ExtraLight.ttf 파일 참조
    )
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        // 타이틀
        Text(
            text = stringResource(id = R.string.profile_edit_screen_text),
            fontSize = 32.sp,  // 글자 크기
            textAlign = TextAlign.Center,
            fontFamily = PretendardRegualr,
            modifier = Modifier.fillMaxWidth()

        )

        Spacer(modifier = Modifier.height(16.dp))  // 간격 추가

        Text(
            text = stringResource(id=R.string.profile_edit_screen_description),
            fontSize = 12.sp,
            textAlign = TextAlign.Left,
            fontFamily = PretendardRegualr,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(4.dp))  // 간격 추가
        // 이름 입력 필드 (아이콘 포함)
        OutlinedTextField(
            value = user_name,
            onValueChange = { user_name = it },
            label = { Text(text = stringResource(id = R.string.name),fontFamily = PretendardRegualr) },  // "이름" 레이블
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.peopleicon),
                    contentDescription = "Name Icon"
                )
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))  // 간격 추가

        // 생년월일 입력 필드 (아이콘 포함)
        OutlinedTextField(
            value = user_birthdate,
            onValueChange = { user_birthdate = it },
            label = { Text(text = stringResource(id = R.string.age),fontFamily = PretendardRegualr) },  // "생년월일" 레이블
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.dateicon),
                    contentDescription = "Birthdate Icon"
                )
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))  // 간격 추가

        // 성별 입력 필드 (아이콘 포함)
        OutlinedTextField(
            value = user_gender,
            onValueChange = { user_gender = it },
            label = { Text(text = stringResource(id = R.string.gender),fontFamily = PretendardRegualr) },  // "성별" 레이블
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.gendericon),
                    contentDescription = "Gender Icon"
                )
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))  // 간격 추가

        // 질환 입력 필드 (아이콘 포함)
        OutlinedTextField(
            value = user_disease,
            onValueChange = { user_disease = it },
            label = { Text(text = stringResource(id = R.string.disease),fontFamily = PretendardRegualr) },  // "질환" 레이블
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.bandageicon),
                    contentDescription = "Disease Icon"
                )
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))  // 간격 추가

        // 병원 입력 필드 (아이콘 포함)
        OutlinedTextField(
            value = hospital,
            onValueChange = { hospital = it },
            label = { Text(text = stringResource(id = R.string.hospital),fontFamily = PretendardRegualr) },  // "병원" 레이블
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.hospitalicon),
                    contentDescription = "Hospital Icon"
                )
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))  // 간격 추가

        // 복용 약 입력 필드 (아이콘 포함)
        OutlinedTextField(
            value = pill,
            onValueChange = { pill = it },
            label = { Text(text = stringResource(id = R.string.pill),fontFamily = PretendardRegualr) },  // "복용 약" 레이블
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.pillicon),
                    contentDescription = "Pill Icon"
                )
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))  // 간격 추가

        // 전화번호 입력 필드 (아이콘 포함)
        OutlinedTextField(
            value = user_protector,
            onValueChange = { user_protector = it },
            label = { Text(text = stringResource(id = R.string.protector),fontFamily = PretendardRegualr) },  // "전화번호" 레이블
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.phoneicon),
                    contentDescription = "Phone Icon"
                )
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(32.dp))  // 간격 추가

        // 저장 버튼
        Button(
            onClick = {
                // 저장 로직 처리
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(id = R.string.save))
        }
        Spacer(modifier = Modifier.height(5.dp))  // 간격 추가
        Text(
            text = stringResource(id = R.string.profile_edit_screen_to_sign_up),
            fontSize = 12.sp,  // 글자 크기
            textAlign = TextAlign.Center,
            fontFamily = PretendardRegualr,
            modifier = Modifier.fillMaxWidth()

        )
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileEditScreenPreview() {
    PushOfLifeTheme {
        ProfileEditScreen()
    }
}
