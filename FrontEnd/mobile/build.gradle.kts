plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("com.google.gms.google-services")

}

android {
    namespace = "com.example.pushoflife"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.pushoflife"
        minSdk = 29
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        // Google Map API 키 설정 추가
        val googleMapKey = project.findProperty("GOOGLE_MAP_KEY") as String? ?: ""
        buildConfigField("String", "GOOGLE_MAP_KEY", "\"$googleMapKey\"")
        manifestPlaceholders["GOOGLE_MAP_KEY"] = googleMapKey

        // TMap API 키 설정 추가
        val tMapKey = project.findProperty("T_MAP_KEY") as String? ?: ""
        buildConfigField("String", "T_MAP_KEY", "\"$tMapKey\"")
        manifestPlaceholders["T_MAP_KEY"] = tMapKey

        ndk {
            abiFilters.add("arm64-v8a")
            abiFilters.add("armeabi-v7a")
        }
        val twilioAccountSID = project.findProperty("TWILIO_ACCOUNT_SID") as String? ?: ""
        val twilioAuthToken = project.findProperty("TWILIO_AUTH_TOKEN") as String? ?: ""
        buildConfigField ("String", "TWILIO_ACCOUNT_SID", "\"${twilioAccountSID}\"")
        buildConfigField ("String", "TWILIO_AUTH_TOKEN", "\"${twilioAuthToken}\"")
        val backServer = project.findProperty("BACK_SERVER") as String? ?: ""
        val fromNumber = project.findProperty("FROM_NUMBER") as String? ?: ""
        buildConfigField ("String", "BACK_SERVER", "\"${backServer}\"")
        buildConfigField ("String", "FROM_NUMBER", "\"${fromNumber}\"")

    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    // Jetpack Compose 활성화
    buildFeatures {
        compose = true
        buildConfig = true
        dataBinding = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "META-INF/DEPENDENCIES"

        }
    }

}

dependencies {
    implementation(platform(libs.firebase.bom)) // BOM을 사용해 의존성 버전 관리
    implementation(libs.firebase.messaging)
    implementation(libs.firebase.analytics)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.play.services.wearable)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    wearApp(project(":wear"))
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.play.services.location)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation(libs.twilio.sdk)
    implementation(libs.gms.maps)
    implementation(libs.gms.location)
    implementation(files("libs/tmap-sdk-1.4.aar"))
    implementation(files("libs/vsm-tmap-sdk-v2-android-1.6.60.aar"))

    // Koin Core 모듈 (필수)
    implementation("io.insert-koin:koin-core:3.4.0")

    // Koin Android 모듈 (Android 통합에 필요한 기능 포함)
    implementation("io.insert-koin:koin-android:3.4.0")

    // 다른 의존성들
//    implementation("androidx.databinding:databinding-runtime:7.0.0") // Android Gradle Plugin 버전에 맞게 설정
    implementation("androidx.compose.material3:material3:1.1.0")
}