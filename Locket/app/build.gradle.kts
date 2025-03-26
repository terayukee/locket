import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
}
val properties = Properties().apply {
    load(rootProject.file("apikey.properties").inputStream())
}

val nativeApiKey: String = properties.getProperty("native_api_key") ?: ""
val baseUrl: String = properties.getProperty("base_url") ?: ""
val manifestNativeAppKey: String = properties.getProperty("manifest_native_app_key") ?: ""

android {
    namespace = "com.ssafy.locket"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.ssafy.locket"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        renderscriptTargetApi = 18
        renderscriptSupportModeEnabled = true

        buildConfigField("String", "NATIVE_API_KEY", nativeApiKey)
        buildConfigField("String", "BASE_URL", baseUrl)
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            manifestPlaceholders["NATIVE_API_KEY"] = manifestNativeAppKey
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isMinifyEnabled = false
            manifestPlaceholders["NATIVE_API_KEY"] = manifestNativeAppKey
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    viewBinding {
        enable = true
    }
    buildFeatures {
        buildConfig = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // https://github.com/square/retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    //gson. json 파싱용
    implementation ("com.google.code.gson:gson:2.10.1")
    // https://github.com/square/retrofit/tree/master/retrofit-converters/gson
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    //dagger와 hilt
    implementation("com.google.dagger:hilt-android:2.51.1")
    kapt("com.google.dagger:hilt-android-compiler:2.51.1")
    //nav바 이동
    implementation("androidx.navigation:navigation-fragment:2.8.9")
    implementation("androidx.navigation:navigation-ui:2.8.9")

    implementation ("com.github.bumptech.glide:glide:4.16.0")
    //chart 생성
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")

    // calendar
    implementation("com.kizitonwose.calendar:view:2.5.4")

    implementation ("com.kakao.sdk:v2-user:2.20.1")

    //지문인식
    implementation ("androidx.biometric:biometric:1.0.1")
}

kapt {
    correctErrorTypes = true
}