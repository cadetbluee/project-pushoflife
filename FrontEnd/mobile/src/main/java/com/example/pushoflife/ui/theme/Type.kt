
package com.example.pushoflife.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.pushoflife.R

// Pretendard 폰트 설정
val PretendardFamily = FontFamily(
    Font(R.font.pretendard_regular, FontWeight.Normal), // 폰트 파일 추가
    Font(R.font.pretendard_bold, FontWeight.Bold)       // 필요한 경우 다른 스타일의 폰트도 추가
)
// Set of Material typography styles to start with
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = PretendardFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
    ,
    titleLarge = TextStyle(
        fontFamily = PretendardFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),labelSmall = TextStyle(
        fontFamily = PretendardFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
/* Other default text styles to override

*/
)