package com.example.pushoflife.ui.serviceguide

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.example.pushoflife.ui.theme.PushOfLifeTheme
import com.example.pushoflife.R

@Composable
fun ServiceScreen() {
    Text(
        text = stringResource(id = R.string.service_screen_text),
        fontSize = 32.sp,  // 글자 크기
        textAlign = TextAlign.Center
    )

}

@Preview(showBackground = true)
@Composable
fun ServiceScreenPreview() {
    PushOfLifeTheme {
        ServiceScreen()
    }
}