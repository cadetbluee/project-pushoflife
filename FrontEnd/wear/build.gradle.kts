plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
}

android {
    namespace = "com.example.pushoflife"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.pushoflife"
        minSdk = 33
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        vectorDrawables {
            useSupportLibrary = true
        }

        // TMap API 키 설정 추가
        val tMapKey = project.findProperty("T_MAP_KEY") as String? ?: ""
        buildConfigField("String", "T_MAP_KEY", "\"$tMapKey\"")
        manifestPlaceholders["T_MAP_KEY"] = tMapKey

        ndk {
            abiFilters.add("arm64-v8a")
            abiFilters.add("armeabi-v7a")
        }

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
    buildFeatures {
        buildConfig = true
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(libs.gms.location)
    implementation(libs.play.services.location)
    implementation(libs.material)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.core.ktx)
    implementation(libs.play.services.wearable)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.compose.material)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose.android)
    implementation(libs.androidx.material3.android)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    wearApp(project(":wear"))
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation(files("libs/tmap-sdk-1.4.aar"))
    implementation(files("libs/vsm-tmap-sdk-v2-android-1.6.60.aar"))
    implementation("androidx.compose.material3:material3:1.1.0")
    implementation(libs.wear.tiles)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.horologist.tiles)
}
