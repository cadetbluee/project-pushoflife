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
import com.example.pushoflife.ui.guidecpr.CprGuideScreen
import com.example.pushoflife.ui.theme.PushOfLifeTheme
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.activity.result.contract.ActivityResultContracts
import android.widget.Toast
import android.os.Build
import androidx.core.content.ContextCompat
import android.content.pm.PackageManager
import android.Manifest
import android.content.BroadcastReceiver
import com.google.firebase.messaging.FirebaseMessaging
import android.util.Log
import com.google.android.gms.tasks.Task
import androidx.lifecycle.lifecycleScope
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.background
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.example.pushoflife.ui.aedLocation.AedLocationScreen
import com.example.pushoflife.ui.user.LoginScreen
import com.example.pushoflife.ui.user.SignUpScreen
import android.content.Context
import android.util.Base64
//import android.util.Log
import java.security.MessageDigest
import android.content.Intent
import android.content.IntentFilter
import android.location.Geocoder
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.platform.LocalContext
import com.example.pushoflife.bluetooth.BleDeviceService
import com.example.pushoflife.bluetooth.BleManagerViewModel
import com.example.pushoflife.data.datastore.EmergencyPreferences
import com.example.pushoflife.data.datastore.TokenPreferences
import com.example.pushoflife.data.datastore.UserPreferences
import com.example.pushoflife.network.CurrentLocationRequest
import com.example.pushoflife.network.RetrofitClient
import com.example.pushoflife.network.SendLocationRequest
import com.example.pushoflife.ui.aedLocation.AedLocationActivity
import com.example.pushoflife.service.FallDetectionService
import com.example.pushoflife.service.LocationUpdatesService
import com.example.pushoflife.utils.TwilioUtils
import com.example.pushoflife.utils.isWearDeviceConnected
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.first
import org.koin.androidx.viewmodel.ext.android.viewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.nio.ByteBuffer
import java.util.Locale

suspend fun getAddressFromLocation(context: Context, latitude: Double, longitude: Double): String? {
    return try {
        val geocoder = Geocoder(context,Locale.KOREAN)
        val addresses = geocoder.getFromLocation(latitude, longitude, 1)
        if (addresses.isNullOrEmpty()) {
            "주소를 찾을 수 없습니다."
        } else {
            addresses[0].getAddressLine(0) // 도로명 주소 반환
        }
    } catch (e: Exception) {
        e.printStackTrace()
        "주소를 찾을 수 없습니다."
    }
}
suspend fun prepareEmergencyMessage(context: Context) {
    val tokenPreferences = TokenPreferences(context)
    val userPreferences = UserPreferences(context)
    val userProfile = userPreferences.getUserProfile().first()

    // 위도와 경도 가져오기
    val (latitude, longitude) = tokenPreferences.getLocation().first()

    // 위도와 경도를 주소로 변환
    val address = if (latitude != null && longitude != null) {
        getAddressFromLocation(context, latitude, longitude)
    } else {
        "주소를 찾을 수 없습니다."
    }

    // 메시지 내용 빌드
    val messageContent = buildString {
        if (userProfile.name.isNotEmpty()) append("${userProfile.name}님의 비상 연락처입니다.\n")
        else append("비상 연락처입니다.\n")
        append("환자가 쓰러져 자동 신고되었습니다.\n")
        append("환자 위치: $address")
    }

    // 메시지를 DataStore에 저장
    userPreferences.saveEmergencyMessage(messageContent)
}
fun getKeyHash(context: Context): String? {
    try {
        val info = context.packageManager.getPackageInfo(context.packageName, android.content.pm.PackageManager.GET_SIGNATURES)
        for (signature in info.signatures) {
            val md = MessageDigest.getInstance("SHA")
            md.update(signature.toByteArray())
            return Base64.encodeToString(md.digest(), Base64.NO_WRAP)
        }
    } catch (e: Exception) {
        Log.e("KeyHash", "Unable to get KeyHash.", e)
    }
    return null
}
// 폰트 정의
val PoppinsExtraLight = FontFamily(
    Font(R.font.poppins_extralight, FontWeight.ExtraLight)
)
val PretendardSemiBold = FontFamily(
    Font(R.font.pretendard_semibold, FontWeight.SemiBold)
)
val PretendardRegular = FontFamily(
    Font(R.font.pretendard_regular, FontWeight.Normal)
)


class MainActivity : ComponentActivity() {
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var messageClient: MessageClient
    override fun onResume() {
        super.onResume()
        stopSirenInService()
    }

    private fun stopSirenInService() {
        val stopSirenIntent = Intent(this, FallDetectionService::class.java).apply {
            action = FallDetectionService.ACTION_STOP_SIREN
        }
        startService(stopSirenIntent)
    }
    private val requestMultiplePermissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        permissions.entries.forEach {
            val permission = it.key
            val isGranted = it.value
            if (isGranted) {
                Log.d("Permissions", "$permission granted.")
            } else {
                Log.d("Permissions", "$permission denied.")
            }
        }
    }
    private fun askPermissions() {
        val permissionsToRequest = mutableListOf<String>()

        // 알림 권한 요청 (Android 13 이상)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
        // Bluetooth 연결 권한 요청
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.BLUETOOTH)
        }
        // Bluetooth 연결 권한 요청
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.BLUETOOTH_ADMIN)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.BLUETOOTH_SCAN)
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.BLUETOOTH_CONNECT)
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADVERTISE) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.BLUETOOTH_ADVERTISE)
            }
        }
        // 위치 권한 요청
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        // 필요한 권한이 있다면 요청, 없다면 바로 서비스 시작
        if (permissionsToRequest.isNotEmpty()) {
            requestMultiplePermissionsLauncher.launch(permissionsToRequest.toTypedArray())
        } else {
            startLocationService() // 모든 권한이 이미 허용된 경우 서비스 바로 시작
        }
    }

    // 포그라운드 권한 요청 예시
    private val requestForegroundLocationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // 포그라운드 위치 권한이 허용된 경우 백그라운드 위치 권한 요청
            requestBackgroundLocationPermission()
        } else {
            Toast.makeText(this, "Foreground location permission is required.", Toast.LENGTH_SHORT).show()
        }
    }

    // 백그라운드 권한 요청 함수
    private fun requestBackgroundLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Android 11 이상에서는 설정 화면으로 이동
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", packageName, null)
            }
            startActivity(intent)
            Toast.makeText(this, "Please enable 'Allow all the time' for location access.", Toast.LENGTH_LONG).show()
        }
    }

    // 포그라운드 위치 권한 요청 실행
    fun requestLocationPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestForegroundLocationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            requestBackgroundLocationPermission()
        }
    }


    private fun startLocationService() {
        startService(Intent(this, LocationUpdatesService::class.java))
    }
    private lateinit var userPreferences: UserPreferences
    private var isFallDetectionEnabled by mutableStateOf(false)
    private var emergency by mutableStateOf(false)
    private lateinit var emergencyPreferences: EmergencyPreferences
    private val bleManagerViewModel: BleManagerViewModel by viewModel()

    // 마지막 메시지 수신 시간을 기록하는 변수
    private var lastMessageTime: Long = 0L

    private val messageListener = MessageClient.OnMessageReceivedListener { messageEvent ->
        if (messageEvent.path == "/emergency_path") {
            val currentTime = System.currentTimeMillis()

            // 마지막 메시지 수신 후 5초가 지나지 않았으면 무시
            if (currentTime - lastMessageTime < 5000) {
                Log.d("MessageReceiverMain", "Message ignored to prevent duplicate alerts.")
                return@OnMessageReceivedListener
            }

            lastMessageTime = currentTime // 메시지 수신 시간을 업데이트

            val receivedMessage = String(messageEvent.data)
            Log.d("MessageReceiverMain", "Received message: $receivedMessage")

            // DataStore에서 FCM token을 가져와서 API 요청
            GlobalScope.launch(Dispatchers.IO) {
                val tokenPreferences = TokenPreferences(context = applicationContext)
                val fcmToken = tokenPreferences.getFcmToken().first()

                // 서버로 전송할 LocationRequest 객체 생성
                val sendRequest = SendLocationRequest(
                    fcm_token = fcmToken ?: "",
                )

                RetrofitClient.apiService.askForHelp(sendRequest).enqueue(object :
                    Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        if (response.isSuccessful) {
                            Log.d("AskForHelp", "My Location data sent successfully.")
                        } else {
                            Log.e("AskForHelp", "Failed to send location data. Response code: ${response.code()}")
                        }
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        Log.e("AskForHelp", "Error sending location data: ${t.message}")
                    }
                })
            }
            CoroutineScope(Dispatchers.IO).launch {
                val userPreferences = UserPreferences(context = applicationContext)
                val messageContent = userPreferences.emergencyMessage.first() // 저장된 메시지 불러오기
                // Broadcast 전송
                val intent = Intent("com.example.pushoflife.ACTION_WATCH_ALERT")
                sendBroadcast(intent)
                TwilioUtils.sendSmsWithRetrofit(
                    "", // 수신자 번호
                    BuildConfig.FROM_NUMBER,
                    messageContent,
                    BuildConfig.TWILIO_ACCOUNT_SID,
                    BuildConfig.TWILIO_AUTH_TOKEN
                )
                Log.d("FallDetectionService", "Emergency SMS sent: $messageContent")
            }
        } else if(messageEvent.path == "/CPR_start"){
            CoroutineScope(Dispatchers.IO).launch {
                handleFeedbackAlertReceived()
                Log.d("CPR_start", "CPR시작 버튼 누름!, 주변 워치에게 전달")
            }
        } else if(messageEvent.path == "/emergency_call"){
            CoroutineScope(Dispatchers.IO).launch {
                val userPreferences = UserPreferences(context = applicationContext)
                val messageContent = userPreferences.emergencyMessage.first() // 저장된 메시지 불러오기
                // Broadcast 전송
                val intent = Intent("com.example.pushoflife.ACTION_WATCH_ALERT")
                sendBroadcast(intent)
                TwilioUtils.sendSmsWithRetrofit(
                    "", // 수신자 번호
                    BuildConfig.FROM_NUMBER,
                    messageContent,
                    BuildConfig.TWILIO_ACCOUNT_SID,
                    BuildConfig.TWILIO_AUTH_TOKEN
                )
                Log.d("FallDetectionService", "Emergency SMS sent: $messageContent")
            }
        }
    }
    private lateinit var locationData: ByteArray // 클래스 레벨 선언으로 전환

    private lateinit var watchAlertReceiver: BroadcastReceiver
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        askPermissions()
        messageClient = Wearable.getMessageClient(this)
        messageClient.addListener(messageListener)
        val tokenPreferences = TokenPreferences(applicationContext)
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task: Task<String> ->
                if (!task.isSuccessful) {
                    Log.w("MainActivity", "Fetching FCM registration token failed", task.exception)
                    return@addOnCompleteListener
                }

                // Get new FCM registration token

                val token = task.result
                // Use GlobalScope to launch a coroutine
                GlobalScope.launch(Dispatchers.IO) {
                    tokenPreferences.saveFcmToken(token) // Save it to DataStore
                    Log.d("TokenSaveCheck", "Saved token: $token")
                }
                Log.d("MainActivity", "FCM Registration Token: $token")
            }

        firebaseAnalytics = Firebase.analytics
        userPreferences = UserPreferences(this)
        emergencyPreferences = EmergencyPreferences(this)

        // 긴급 메시지 준비 및 locationData 설정
        lifecycleScope.launch {
            prepareEmergencyMessage(this@MainActivity)

            // Retrieve location data from DataStore as a Pair of Doubles
            tokenPreferences.getLocation().collect { location ->
                val latitude = location.first ?: 0.0
                val longitude = location.second ?: 0.0

                locationData = getLocationDataAsByteArray(latitude, longitude)
                Log.d("LocationData", "LocationData set with latitude: $latitude, longitude: $longitude")
            }
        }
        // DataStore에서 허용 여부를 확인하여 서비스 시작
        lifecycleScope.launch {
            userPreferences.allowDetection.collect { allowDetection ->
                val serviceIntent = Intent(this@MainActivity, FallDetectionService::class.java)
                isFallDetectionEnabled = allowDetection
                if (allowDetection) {
                    startForegroundService(serviceIntent)
                    Log.d("MainActivity", "Fall detection service started.")
                } else {
                    stopService(serviceIntent)
                    Log.d("MainActivity", "Fall detection service stopped.")
                }
            }
        }
        lifecycleScope.launch {
            emergencyPreferences.getLocation().collect { (latitude, longitude) ->
                emergency = latitude != null && longitude != null
                Log.d("la&lo","$emergency, $latitude, $longitude")
            }
        }

        enableEdgeToEdge()
        // BroadcastReceiver 등록
        registerWatchAlertReceiver()

        // BleDeviceService 시작
        val bleDeviceServiceIntent = Intent(this, BleDeviceService::class.java)
        startService(bleDeviceServiceIntent)

        setContent {
            PushOfLifeTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "main_screen") {
                    composable("main_screen") {
                        MainScreen(
                            navController,
                            isFallDetectionEnabled = isFallDetectionEnabled,
                            emergency = emergency,
                            onToggleFallDetection = { enabled ->
                                lifecycleScope.launch {
                                    userPreferences.setAllowDetection(enabled)
                                }
                            }
                        )
                    }
                    composable("service_screen") { ServiceScreen(navController) }
                    composable("profile_edit_screen") { ProfileEditScreen(navController) }
                    composable("sign_up_screen") { SignUpScreen(navController) }
                    composable("login_screen") { LoginScreen(navController) }
                    composable("cpr_guide_screen") { CprGuideScreen(navController) }
                    composable("aed_location_screen") { AedLocationScreen(navController) }
                }
            }
        }

    }

    private fun registerWatchAlertReceiver() {
        val watchAlertReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                Log.d("MainActivity","${intent.action}")
                try {
                    if (intent.action == "com.example.pushoflife.ACTION_WATCH_ALERT") {
                        Log.d("MainActivity", "Watch alert received")
//                        handleWatchAlertReceived() // ViewModel과 상호작용을 직접 호출을 통해 처리
                    } else if(intent.action == "com.example.pushoflife.ACTION_FEEDBACK_START"){
                        Log.d("MainActivity", "CPR 피드백 시작 알림 받음")
                        handleFeedbackAlertReceived() // ViewModel과 상호작용을 직접 호출을 통해 처리
                    }
                    else {
                        Log.e("MainActivity", "Unexpected action received: ${intent.action}")
                    }
                } catch (e: Exception) {
                    Log.e("MainActivity", "Error processing broadcast", e)
                }
            }
        }

        // BroadcastReceiver 등록
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(
                watchAlertReceiver,
                IntentFilter("com.example.pushoflife.ACTION_WATCH_ALERT"),
                Context.RECEIVER_NOT_EXPORTED
            )
        } else {
            registerReceiver(
                watchAlertReceiver,
                IntentFilter("com.example.pushoflife.ACTION_WATCH_ALERT")
            )
        }
    }

    fun getLocationDataAsByteArray(latitude: Double, longitude: Double): ByteArray {
        val byteBuffer = ByteBuffer.allocate(16)
        byteBuffer.putDouble(latitude)
        byteBuffer.putDouble(longitude)
        return byteBuffer.array()
    }

    // ViewModel과 상호작용하는 메서드
    private fun handleWatchAlertReceived() {
        Log.d("MainActivity", "낙상 감지 알림 탐지")
        bleManagerViewModel.onWatchAlertReceived(locationData)
    }
    // ViewModel과 상호작용하는 메서드
    private fun handleFeedbackAlertReceived() {
        Log.d("MainActivity", "CPR피드백 알림 탐지")
        bleManagerViewModel.onWatchAlertReceived(getLocationDataAsByteArray(10000.11,10000.11))
    }

    override fun onDestroy() {
        super.onDestroy()
        // 초기화된 경우에만 해제
        if (::watchAlertReceiver.isInitialized) {
            unregisterReceiver(watchAlertReceiver)
        }
        messageClient.removeListener(messageListener)
        // 위치 추적 서비스 중지
        stopService(Intent(this, LocationUpdatesService::class.java))
    }
}

@Composable
fun MainScreen(navController: NavHostController, modifier: Modifier = Modifier,isFallDetectionEnabled: Boolean,emergency:Boolean,
               onToggleFallDetection: (Boolean) -> Unit) {
    // 현재 카드 인덱스를 저장하는 상태
    var currentCardIndex by remember { mutableStateOf(1) }
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    // 위도와 경도 값을 위한 상태
    var latitude by remember { mutableStateOf<Double?>(null) }
    var longitude by remember { mutableStateOf<Double?>(null) }

    LaunchedEffect(emergency) {
        // EmergencyPreferences에서 위치 데이터를 가져옴
        val emergencyPreferences = EmergencyPreferences(context)
        emergencyPreferences.getLocation().collect { (lat, lon) ->
            latitude = lat
            longitude = lon
            Log.d("la&lo","$latitude, $longitude")
        }


    }

    // Wear OS 장치 연결 여부 확인
    val isWearDeviceConnected = isWearDeviceConnected(context)
//    Log.d("isWearDeviceCOnnected", "$isWearDeviceConnected")
    // 응급상황 임시 변수
//    var emergency by remember { mutableStateOf(false) }

    if (isWearDeviceConnected && isFallDetectionEnabled) {
        onToggleFallDetection(false)
    }
    // 3초마다 카드 인덱스를 변경하는 LaunchedEffect
    LaunchedEffect(key1 = currentCardIndex) {
        while (true) {
            delay(4000L)  // 4초마다 실행
            currentCardIndex = (currentCardIndex % 3) + 1  // 1 -> 2 -> 3 순환
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(start = 20.dp, top = 40.dp, end = 20.dp, bottom = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 상단의 'Push of Life' 텍스트와 이미지
        Row(
            verticalAlignment = Alignment.CenterVertically,  // 수직으로 가운데 정렬
            modifier = Modifier
                .fillMaxWidth(),
        ) {

            Text(
                text = stringResource(id = R.string.app_name),
                fontFamily = PoppinsExtraLight,
                fontSize = 30.sp,
                textAlign = TextAlign.Start,
                modifier = Modifier.weight(1f),
                letterSpacing = (-1).sp
            )

            // 이미지 클릭 시 프로필 수정 화면으로 이동
            Image(
                painter = painterResource(id = R.drawable.profileicon),  // VectorDrawable 사용
                contentDescription = "Profile Icon",
                modifier = Modifier
                    .size(35.dp)  // 이미지 크기 설정
                    .clickable { navController.navigate("profile_edit_screen") },  // 클릭 이벤트 처리
                contentScale = ContentScale.Fit
            )
        }

        Divider(
            color = Color.Gray,            // 선 색상
            thickness = 0.5.dp,              // 선 두께
            modifier = Modifier.padding(vertical = 8.dp)  // 위아래 여백
        )


        if (emergency) {
            // 응급상황 시 조건부 컴포넌트

            Spacer(modifier = Modifier.height(10.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable {
                        val intent = Intent(context, AedLocationActivity::class.java)
                        intent.putExtra("emergency", emergency)
                        intent.putExtra("patient_latitude", latitude) // 임시 위도
                        intent.putExtra("patient_longitude", longitude) // 임시 경도
                        context.startActivity(intent)
                    }
            ) {
                Image(
                    painter = painterResource(id = R.drawable.emergency),
                    contentDescription = "응급상황 발생",
                    modifier = Modifier
                        .fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                Text(
                    text = "도움이 필요합니다.\n" +
                            "근처에 심정지 환자가 있습니다.\n" +
                            "버튼을 터치하면 구조를 시작합니다.",
                    fontSize = 20.sp,
                    fontFamily = PretendardSemiBold,
                    lineHeight = 33.sp,
                    letterSpacing = (-0.5).sp,
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier
                        .padding(top = 15.dp, start = 15.dp)
                )

            }
        }

        Spacer(modifier = Modifier.height(10.dp))
        if (!isWearDeviceConnected) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .clip(RoundedCornerShape(12.dp))
            ) {
                // 배경 이미지

                // 토글 및 상태 텍스트
                Row(
                    modifier = Modifier
                        .align(Alignment.TopStart), // Box 안에서 토글 위치 설정
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    // 토글 버튼
                    Switch(
                        checked = isFallDetectionEnabled,
                        onCheckedChange = { enabled ->
                            onToggleFallDetection(enabled)
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.onSecondary,         // 활성화된 스위치 색상
                            uncheckedThumbColor = MaterialTheme.colorScheme.onSecondary,      // 비활성화된 스위치 색상
                            checkedTrackColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f),  // 활성화된 트랙 색상
                            uncheckedTrackColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f),  // 비활성화된 트랙 색상
                        ),
                        modifier = Modifier
                            .padding(end = 10.dp)
                    )

                    // 상태에 따라 텍스트 표시
                    Text(
                        text = if (isFallDetectionEnabled) "낙상을 감지 중이에요!" else "낙상 감지 기능이 꺼져 있어요!",
                        fontSize = 16.sp,
                        fontFamily = PretendardRegular,
                        letterSpacing = (-0.5).sp,
//                    color = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        }

        // 서비스 가이드 컴포넌트
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp)
                .clip(RoundedCornerShape(12.dp))
                .clickable { navController.navigate("service_screen") }  // 스크린 이동
        ) {
            Image(
                painter = painterResource(id = R.drawable.watch_guide),
                contentDescription = "워치 가이드",
                modifier = Modifier
                    .fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Text(
                text = "PushOfLife 워치 가이드",
                fontSize = 20.sp,
                fontFamily = PretendardSemiBold,
                letterSpacing = (-0.5).sp,
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier
                    .padding(top = 15.dp, start = 15.dp)
            )

        }

        Spacer(modifier = Modifier.height(10.dp))

        // AED 컴포넌트
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp)
                .clip(RoundedCornerShape(12.dp))
                .clickable {
                    // XML Activity로 이동
                    val intent = Intent(context, AedLocationActivity::class.java)
                    context.startActivity(intent)
                }
        ) {
            Image(
                painter = painterResource(id = R.drawable.aed_location),
                contentDescription = "AED 위치",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Text(
                text = "AED 위치",
                fontSize = 20.sp,
                fontFamily = PretendardSemiBold,
                letterSpacing = (-0.5).sp,
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.padding(top = 15.dp, start = 15.dp)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // CPR 가이드 컴포넌트
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp)
                .clip(RoundedCornerShape(12.dp))
                .clickable { navController.navigate("cpr_guide_screen") }  // 스크린 이동
        ) {
            Image(
                painter = painterResource(id = R.drawable.cpr_guide),
                contentDescription = "CPR 가이드",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Text(
                text = "CPR, 어떻게 하나요?",
                fontSize = 20.sp,
                fontFamily = PretendardSemiBold,
                letterSpacing = (-0.5).sp,
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier
                    .padding(top = 15.dp, start = 15.dp)
            )

        }

        Spacer(modifier = Modifier.height(10.dp))

        // 상태에 따라 다른 카드 컴포넌트를 렌더링
        when (currentCardIndex) {
            1 -> CardComponent1()
            2 -> CardComponent2()
            3 -> CardComponent3()
        }

        Spacer(modifier = Modifier.height(50.dp))
    }
}

// 카드 컴포넌트 1
@Composable
fun CardComponent1() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(260.dp)
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
                fontFamily = PretendardSemiBold,
                letterSpacing = (-0.5).sp,
                color = Color(0xFF3A3A3C),
                textAlign = TextAlign.Center,
            )

            // 중앙 이미지
            Image(
                painter = painterResource(id = R.drawable.card_cprwhere),
                contentDescription = "CPR 가이드",
                modifier = Modifier
                    .width(270.dp)
                    .height(150.dp)
            )

            // 하단 텍스트
            Text(
                text = "명치에 세 손가락을 얹고 \n 얼굴과 가장 가까운 손가락의 위치를 압박하세요!",
                fontSize = 14.sp,
                fontFamily = PretendardRegular,
                letterSpacing = (-0.5).sp,
                color = Color(0xFF3A3A3C),
                textAlign = TextAlign.Center,
            )
        }
    }
}

// 카드 컴포넌트 2
@Composable
fun CardComponent2() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(260.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(color = Color(0xFFD5DCFF))
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
                text = "AED, 왜 사용해야 할까요?",
                fontSize = 20.sp,
                fontFamily = PretendardSemiBold,
                letterSpacing = (-0.5).sp,
                color = Color(0xFF3A3A3C),
                textAlign = TextAlign.Center,
            )

            // 중앙 이미지
            Image(
                painter = painterResource(id = R.drawable.card_aedwhy),
                contentDescription = "AED 설명",
                modifier = Modifier
                    .width(270.dp)
                    .height(150.dp)
            )

            // 하단 텍스트
            Text(
                text = "심장 발작 시 전기 충격을 주어\n정상 리듬으로 되돌리기 위해 사용됩니다",
                fontSize = 14.sp,
                fontFamily = PretendardRegular,
                letterSpacing = (-0.5).sp,
                color = Color(0xFF3A3A3C),
                textAlign = TextAlign.Center,
            )
        }
    }
}

// 카드 컴포넌트 3
@Composable
fun CardComponent3() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(260.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(color = Color(0xFFFFC1C1))
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
                text = "CPR, 왜 필요할까요?",
                fontSize = 20.sp,
                fontFamily = PretendardSemiBold,
                letterSpacing = (-0.5).sp,
                color = Color(0xFF3A3A3C),
                textAlign = TextAlign.Center,
            )

            // 중앙 이미지
            Image(
                painter = painterResource(id = R.drawable.card_cprwhy),
                contentDescription = "CPR 설명",
                modifier = Modifier
                    .width(270.dp)
                    .height(150.dp)
            )

            // 하단 텍스트
            Text(
                text = "심장과 뇌에 산소가 포함된 혈액을 공급해주는 CPR!\n 뇌에 피를 공급해 뇌 손상을 방지하려는 목적입니다",
                fontSize = 14.sp,
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
fun MainScreenPreview() {
    PushOfLifeTheme {
        MainScreen(
            navController = rememberNavController(),
            isFallDetectionEnabled = false, // 기본값 설정
            emergency = false,
            onToggleFallDetection = {} // 빈 람다
        )
    }
}
