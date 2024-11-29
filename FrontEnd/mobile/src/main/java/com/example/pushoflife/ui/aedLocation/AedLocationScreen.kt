package com.example.pushoflife.ui.aedLocation

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.pushoflife.R
import com.example.pushoflife.PretendardSemiBold
import com.example.pushoflife.ui.theme.PushOfLifeTheme
import com.example.pushoflife.utils.GetCurrentLocationForMap
import com.skt.tmap.TMapView
import com.skt.tmap.TMapPoint
import androidx.compose.ui.viewinterop.AndroidView
import com.example.pushoflife.BuildConfig
import androidx.compose.runtime.LaunchedEffect
@Composable
fun AedLocationScreen(navController: NavController, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var userLocation by remember { mutableStateOf<TMapPoint?>(null) }
    val initialLocation = TMapPoint(37.5665, 126.9780) // 서울 기본 위치

    Log.d("AedLocationScreen", "T-MAP API Key: ${BuildConfig.T_MAP_KEY}")

    // 현재 위치 가져오기
    GetCurrentLocationForMap(context) { location ->
        userLocation = TMapPoint(location.latitude, location.longitude)
    }

    val tmapView = remember {
        TMapView(context).apply {
            setSKTMapApiKey(BuildConfig.T_MAP_KEY)  // Tmap API 키 설정
        }
    }

    AndroidView(
        factory = { tmapView },
        modifier = modifier.fillMaxSize()
    )

    LaunchedEffect(userLocation) {
        // TMapView 초기화 후 중심 설정
        userLocation?.let { location ->
            tmapView.post {
                tmapView.setCenterPoint(location.longitude, location.latitude)
            }
        } ?: tmapView.post {
            // 기본 위치 설정
            tmapView.setCenterPoint(initialLocation.longitude, initialLocation.latitude)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(vertical = 16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { navController.popBackStack() }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.backicon),
                    contentDescription = "뒤로 가기",
                    modifier = Modifier.size(25.dp)
                )
            }

            Text(
                text = "AED 위치",
                letterSpacing = (-0.5).sp,
                fontSize = 24.sp,
                textAlign = TextAlign.Center,
                fontFamily = PretendardSemiBold,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 40.dp)
            )
        }
    }
}
