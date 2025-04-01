package com.ssafy.locket

import android.app.Application
import android.Manifest
import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.common.util.Utility
import com.ssafy.locket.data.datasource.local.UserDataStoreSource
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject


private const val TAG = "ApplicationClass"
@HiltAndroidApp
class ApplicationClass : Application() {
    @Inject
    lateinit var userDataStoreSource: UserDataStoreSource

    override fun onCreate() {
        super.onCreate()

        KakaoSdk.init(this, BuildConfig.NATIVE_API_KEY)
        FirebaseMessaging
            .getInstance()
            .token
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val token = task.result
                    Log.d(TAG,token)
                    CoroutineScope(Dispatchers.IO).launch {
                        userDataStoreSource.saveFcmToken(token)
                    }
                } else {
                }
            }
    }

    companion object{
        // 모든 퍼미션 관련 배열
        val requiredPermissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.CAMERA,
        )
        // 주문 준비 완료 확인 시간 1분
        const val ORDER_COMPLETED_TIME = 60*1000
//        var keyHash = Utility.getKeyHash(this)
//        Log.d(TAG, "onCreate: keyHash : $keyHash")
    }
}