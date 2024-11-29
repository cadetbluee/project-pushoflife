package com.example.pushoflife

import android.content.Intent
import androidx.wear.protolayout.ActionBuilders
import androidx.wear.protolayout.DimensionBuilders
import androidx.wear.protolayout.LayoutElementBuilders
import androidx.wear.protolayout.ModifiersBuilders
import androidx.wear.protolayout.ResourceBuilders
import androidx.wear.protolayout.TimelineBuilders
import androidx.wear.tiles.TileBuilders
import androidx.wear.tiles.RequestBuilders
import androidx.wear.protolayout.LayoutElementBuilders.Layout
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.tiles.SuspendingTileService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@OptIn(ExperimentalHorologistApi::class)
class CprTileService : SuspendingTileService() {

    override suspend fun tileRequest(requestParams: RequestBuilders.TileRequest): TileBuilders.Tile {
        // 타일에 표시할 데이터를 비동기적으로 로드
        val tileData = loadTileData()

        // 타일 레이아웃 설정
        val layout = tileLayout(tileData)

        // 타일 생성 및 반환
        return TileBuilders.Tile.Builder()
            .setResourcesVersion("1")
            .setTileTimeline(
                TimelineBuilders.Timeline.Builder()
                    .addTimelineEntry(
                        TimelineBuilders.TimelineEntry.Builder()
                            .setLayout(layout)
                            .build()
                    )
                    .build()
            )
            .build()
    }

    // 리소스 요청을 처리하여 아이콘을 리소스에 추가
    override suspend fun resourcesRequest(requestParams: RequestBuilders.ResourcesRequest): ResourceBuilders.Resources {
        return ResourceBuilders.Resources.Builder()
            .setVersion("1")
            .addIdToImageMapping(
                "cpr_icon",
                ResourceBuilders.ImageResource.Builder()
                    .setAndroidResourceByResId(
                        ResourceBuilders.AndroidImageResourceByResId.Builder()
                            .setResourceId(R.drawable.ic_launcher_foreground) // 아이콘 리소스 ID
                            .build()
                    )
                    .build()
            )
            .build()
    }

    // 타일에 표시할 데이터를 로드하는 비동기 함수
    private suspend fun loadTileData(): String = withContext(Dispatchers.IO) {
        "심폐소생 가이드 시작"
    }

    // 타일 레이아웃을 정의하는 함수, 텍스트와 이미지 추가
    private fun tileLayout(text: String): Layout {
        // 앱을 열기 위한 LaunchAction 생성
        val launchAction = ActionBuilders.LaunchAction.Builder()
            .setAndroidActivity(
                ActionBuilders.AndroidActivity.Builder()
                    .setPackageName(packageName)
                    .setClassName(MainActivity::class.java.name)
                    .build()
            )
            .build()

        // Clickable을 설정하기 위한 Modifiers
        val modifiers = ModifiersBuilders.Modifiers.Builder()
            .setClickable(
                ModifiersBuilders.Clickable.Builder()
                    .setId("open_app")
                    .setOnClick(launchAction)
                    .build()
            )
            .build()

        return Layout.Builder()
            .setRoot(
                LayoutElementBuilders.Box.Builder()
                    .setVerticalAlignment(LayoutElementBuilders.VERTICAL_ALIGN_CENTER)
                    .setWidth(DimensionBuilders.expand())
                    .setHeight(DimensionBuilders.expand())
                    .addContent(
                        LayoutElementBuilders.Image.Builder()
                            .setResourceId("cpr_icon") // 리소스에서 아이콘 사용
                            .setWidth(DimensionBuilders.dp(128f)) // 아이콘 크기 조정
                            .setHeight(DimensionBuilders.dp(128f))
                            .build()
                    )
                    .addContent(
                        LayoutElementBuilders.Text.Builder()
                            .setText(text)
                            .setModifiers(modifiers) // Modifiers 설정
                            .build()
                    )
                    .build()
            )
            .build()
    }
}