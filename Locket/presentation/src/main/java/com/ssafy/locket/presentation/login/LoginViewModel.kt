package com.ssafy.locket.presentation.login

import android.app.Activity
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import com.ssafy.locket.data.datasource.local.UserDataStoreSource
import com.ssafy.locket.model.ApiResponse
import com.ssafy.locket.model.login.UserLoginRequest
import com.ssafy.locket.repository.finance.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resumeWithException

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val userDataStoreSource: UserDataStoreSource,
) : ViewModel() {
    // 로그인 상태 Flow (이전 값을 유지하지 않음)
    private val _loginState = MutableStateFlow<Boolean?>(null) // 🔥 null 기본값 추가
    val loginState = _loginState.asStateFlow()

    fun performKakaoLogin(accessToken: String, fcmToken: String) {
        viewModelScope.launch {
            try {
                userRepository.login(UserLoginRequest(accessToken, fcmToken)).collect { response ->
                    when (response) {
                        is ApiResponse.Success -> {
                            Log.d("LoginViewModel", "✅ 로그인 성공 → 홈 화면 이동")
                            _loginState.value = true // ✅ 최신 값 유지
                        }
                        is ApiResponse.Error -> {
                            Log.d("LoginViewModel", "서버 응답 실패 코드: ${response.code}")
                            if (response.code == "401") {
                                Log.d("LoginViewModel", "회원가입이 필요함 → 회원가입 화면 이동")
                                _loginState.value = false // ✅ 최신 값 유지
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("LoginViewModel", "❌ 로그인 처리 중 예외 발생: ${e.message}")
            }
        }
    }

    fun resetLoginState() {
        _loginState.value = null
    }
}