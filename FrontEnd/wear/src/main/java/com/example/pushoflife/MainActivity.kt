
package com.example.pushoflife

import android.app.ActivityManager // 이 줄을 추가하여 ActivityManager를 가져옵니다
import android.content.Context // 이 줄을 추가하여 Context를 가져옵니다
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.text.Layout
import android.util.Log
import android.view.MotionEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.wear.compose.material.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.rememberCoroutineScope
import androidx.wear.compose.material.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.lifecycleScope
import com.example.pushoflife.data.datastore.EmergencyPreferences
import com.example.pushoflife.ui.aedLocation.AedLocationActivity
import com.example.pushoflife.utils.LaunchApp
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {

    private lateinit var sensorServiceIntent: Intent
    private var lastTapTime = 0L // 두 번 터치 감지를 위한 변수
    private var emergency by mutableStateOf(false)
    private lateinit var emergencyPreferences: EmergencyPreferences

    // 권한 요청 런처 설정
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                // 권한이 승인된 경우 처리할 로직
            } else {
                // 권한이 거부된 경우 처리할 로직
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        emergencyPreferences = EmergencyPreferences(this)

        // 안드로이드 13 이상에서 알림 권한 요청
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        // Foreground Service 시작 (이미 실행 중인지 확인)
        sensorServiceIntent = Intent(this, SensorForegroundService::class.java)
        if (!isMyServiceRunning(SensorForegroundService::class.java, this)) {
            startForegroundService(sensorServiceIntent)
        }

        // Jetpack Compose 설정
        setContent {
            MainScreen()
        }
        lifecycleScope.launch {
            emergencyPreferences.getLocation().collect { (latitude, longitude) ->
                emergency = latitude != null && longitude != null
                if (emergency) {
                    val extras = Bundle().apply {
                        putBoolean("emergency", emergency)
                        putDouble("patient_latitude", latitude ?: 0.0)
                        putDouble("patient_longitude", longitude ?: 0.0)
                    }
                    LaunchApp.launchApp(this@MainActivity, AedLocationActivity::class.java, extras)
                    Log.d("la&lo", "$emergency, $latitude, $longitude")
                }
            }
        }
    }

//    override fun onResume() {
//        super.onResume()
//        lifecycleScope.launch {
//            emergencyPreferences.getLocation().collect { (latitude, longitude) ->
//                emergency = latitude != null && longitude != null
//                if (emergency) {
//                    val extras = Bundle().apply {
//                        putBoolean("emergency", emergency)
//                        putDouble("patient_latitude", latitude ?: 0.0)
//                        putDouble("patient_longitude", longitude ?: 0.0)
//                    }
//                    LaunchApp.launchApp(this@MainActivity, AedLocationActivity::class.java, extras)
//                    Log.d("la&lo", "$emergency, $latitude, $longitude")
//                }
//            }
//        }
//    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_DOWN) {
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastTapTime < 300) {
                val intent = Intent(this, CprGuideActivity::class.java)
                startActivity(intent)
            }
            lastTapTime = currentTime
        }
        return super.onTouchEvent(event)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopService(sensorServiceIntent)  // 앱이 종료될 때 서비스 중지
    }
}

// 서비스가 실행 중인지 확인하는 함수
private fun isMyServiceRunning(serviceClass: Class<*>, context: Context): Boolean {
    val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    for (service in manager.getRunningServices(Int.MAX_VALUE)) {
        if (serviceClass.name == service.service.className) {
            return true
        }
    }
    return false
}

val PretendardExtraLight = FontFamily(
    Font(R.font.pretendard_extralight, FontWeight.ExtraLight)
)
val PretendardLight = FontFamily(
    Font(R.font.pretendard_light, FontWeight.Normal)
)

@Composable
fun MainScreen() {
    // ScalingLazyColumn의 스크롤 상태 정의
    val scrollState = rememberScalingLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current  // Compose 내에서 Context 사용

    LaunchedEffect(Unit) {
        scrollState.scrollToItem(0)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // ScalingLazyColumn 구성
        ScalingLazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),
            horizontalAlignment = Alignment.CenterHorizontally,
            state = scrollState // 스크롤 상태 연결
        ) {
            item {
                // 첫 번째 아이템: 메인 아이콘과 CPR 텍스트
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillParentMaxSize() // 화면 전체 채우기
                        .clickable {
                            // 클릭 시 CprGuideActivity로 이동
                            val intent = Intent(context, CprWaitingActivity::class.java)
                            context.startActivity(intent)
                        }
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.main_icon),
                        contentDescription = "Main Icon",
                        modifier = Modifier
                            .size(150.dp)
                            .padding(top = 17.dp)
                    )
                    Text(
                        text = "CPR",
                        fontSize = 70.sp,
                        fontFamily = PretendardExtraLight,
                        color = Color.White,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }

            item {
                // 두 번째 아이템: CPR Practice와 AED Location을 가로로 배치
                Row(
                    modifier = Modifier
                        .fillParentMaxSize()
                        .padding(horizontal = 10.dp),

                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 첫 번째 Box: CPR Practice 이미지와 텍스트
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(end = 8.dp)
                            .clickable {
                                // 클릭 시 CprGuideActivity로 이동
                                val intent = Intent(context, CprGuideActivity::class.java).apply{
                                    putExtra("previous_activity", "MainActivity")  // 이전 액티비티 정보 추가
                                }
                                context.startActivity(intent)
                            }
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.cpr_practice),
                            contentDescription = "CPR Practice",
                            modifier = Modifier.size(80.dp)
                        )
                        Text(
                            text = "CPR 연습",
                            fontSize = 14.sp,
                            fontFamily = PretendardLight,
                            letterSpacing = (-0.5).sp,
                            color = Color.White,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    // 두 번째 Box: AED Location 이미지와 텍스트
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .clickable {
                                // XML Activity로 이동
                                val intent = Intent(context, AedLocationActivity::class.java)
                                context.startActivity(intent)
                            }
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.aed_location),
                            contentDescription = "AED Location",
                            modifier = Modifier.size(80.dp)
                        )
                        Text(
                            text = "AED 위치",
                            fontSize = 14.sp,
                            fontFamily = PretendardLight,
                            letterSpacing = (-0.5).sp,
                            color = Color.White,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        }

        // 스크롤바 표시
        PositionIndicator(
            scalingLazyListState = scrollState // ScalingLazyColumn의 스크롤 상태에 연결
        )
    }
}














































/*
    역수직 관계표
    1위 : 최봉준
    2위 : 구고운
    3위 : 라송연
    4위 : 김예운
    5위 : 윤채영
    6위 : 김수민
 */