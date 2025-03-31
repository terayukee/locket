package com.ssafy.locket

import android.app.Application
import android.Manifest
import android.util.Log
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.common.util.Utility
import dagger.hilt.android.HiltAndroidApp


private const val TAG = "ApplicationClass"
@HiltAndroidApp
class ApplicationClass : Application() {

    override fun onCreate() {
        super.onCreate()

        KakaoSdk.init(this, BuildConfig.NATIVE_API_KEY)
//        var keyHash = Utility.getKeyHash(this)
//        Log.d(TAG, "onCreate: keyHash : $keyHash")
    }
}