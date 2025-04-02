package com.ssafy.locket.presentation.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.locket.data.datasource.local.UserDataStoreSource
import com.ssafy.locket.model.auth.UserJoin
import com.ssafy.locket.model.base.ResponseStatus
import com.ssafy.locket.usecase.auth.LoginUseCase
import com.ssafy.locket.usecase.auth.SignUpUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val signUpUseCase: SignUpUseCase,
    private val dataStore: UserDataStoreSource
) : ViewModel() {
    // 로그인 상태 Flow (이전 값을 유지하지 않음)
    private val _loginState = MutableStateFlow<Boolean?>(null) // 🔥 null 기본값 추가
    val loginState = _loginState.asStateFlow()

    private val _userJoin = MutableStateFlow(
        UserJoin(accesstoken = "", birthYear = 0, fingerprintRegistered = false, paymentPassword = 0, userJob = "")
    )

    val userJoin = _userJoin.asStateFlow()

    fun performKakaoLogin(accessToken: String) {
        viewModelScope.launch {
            try {
                loginUseCase(accessToken).collect { response ->
                    when (response) {
                        is ResponseStatus.Success -> {
                            Log.d("LoginViewModel", "✅ 로그인 성공 → 홈 화면 이동")
                            dataStore.saveJwtToken(response.data.accessToken)
                            _loginState.value = true // ✅ 최신 값 유지
                        }
                        is ResponseStatus.Error -> {
                            Log.d("LoginViewModel", "서버 응답 실패 코드: ${response.error.status}")
                            Log.d("LoginViewModel", "서버 응답 실패 코드: ${response.error.message}")
                            Log.d("LoginViewModel", "서버 응답 실패 코드: ${response.error.code}")
                            if (response.error.status == "401") {
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

    //유저 회원가입 과정
    fun userJoin(userJoin: UserJoin) {
        viewModelScope.launch {
            try {
                signUpUseCase(userJoin.birthYear,userJoin.fingerprintRegistered,userJoin.paymentPassword,userJoin.userJob,userJoin.accesstoken).collect { response ->
                    when (response) {
                        is ResponseStatus.Success -> {
                            Log.d("LoginViewModel","jwt토큰 "+ response.data.accessToken)
                            dataStore.saveJwtToken(response.data.accessToken)
                        }
                        is ResponseStatus.Error -> {
                            Log.d("LoginViewModel", "서버 응답 실패 코드: ${response.error.status}")
                            Log.d("LoginViewModel", "서버 응답 실패 코드: ${response.error.message}")
                            Log.d("LoginViewModel", "서버 응답 실패 코드: ${response.error.code}")
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("LoginViewModel", "❌ 로그인 처리 중 예외 발생: ${e.message}")
            }
        }
    }

    fun updateBirthYear(year: Int) {
        _userJoin.update { it.copy(birthYear = year) }
    }

    fun updateFingerprintRegistered(isRegistered: Boolean) {
        _userJoin.update { it.copy(fingerprintRegistered = isRegistered) }
    }

    fun updateAccessToken(id: String) {
        _userJoin.update { it.copy(accesstoken = id) }
    }

    fun updatePaymentPassword(password: Int) {
        _userJoin.update { it.copy(paymentPassword = password) }
    }

    fun updateUserJob(job: String) {
        _userJoin.update { it.copy(userJob = job) }
    }


}