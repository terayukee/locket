package com.ssafy.locket.presentation.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.locket.data.datasource.local.UserDataStoreSource
import com.ssafy.locket.data.network.request.user.UserLoginRequest
import com.ssafy.locket.repository.finance.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val userDataStoreSource: UserDataStoreSource,
) : ViewModel() {

    // 로그인 결과 상태 (true: 가입 완료, false: 가입 필요)
    private val _loginState = MutableSharedFlow<Boolean>()
    val loginState = _loginState.asSharedFlow()

    fun performKakaoLogin(accessToken: String, fcmToken: String) {
        viewModelScope.launch {
            try {
                userRepository.login(UserLoginRequest(accessToken, fcmToken))
                    .collect { response -> // ✅ Flow 수집
                        when (response) {
                            is ApiResponse.Success -> {
                                val isRegistered = false // 실제로 API에서 받은 데이터로 처리
                                Log.d("SignInFragment","${isRegistered}")
                                if (isRegistered) {
                                    _loginState.emit(true)  // ✅ 가입된 경우 홈 화면 이동
                                } else {
                                    _loginState.emit(false) // ✅ 회원가입 필요
                                }
                            }
                            is ApiResponse.Error -> {
                                Log.e("LoginViewModel", "서버 응답 실패: ${response.message}")
                            }
                        }
                    }
            } catch (e: Exception) {
                Log.e("LoginViewModel", "로그인 처리 중 예외 발생: ${e.message}")
            }
        }
    }
}