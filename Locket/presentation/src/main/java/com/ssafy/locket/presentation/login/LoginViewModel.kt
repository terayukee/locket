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
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "LoginViewModel"
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val signUpUseCase: SignUpUseCase,
    private val dataStore: UserDataStoreSource
) : ViewModel() {
    // 로그인 상태 Flow (이전 값을 유지하지 않음)
    private val _loginState = MutableStateFlow<LoginStatus>(LoginStatus.Idle) // 🔥 null 기본값 추가
    val loginState : StateFlow<LoginStatus> = _loginState.asStateFlow()

    private val _signUpSuccess = MutableStateFlow<Boolean?>(null)
    val signUpSuccess: StateFlow<Boolean?> get() = _signUpSuccess

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
                            _loginState.value = LoginStatus.Success
                            dataStore.saveJwtToken("Bearer " + response.data.accessToken)
                        }
                        is ResponseStatus.Error -> {
                            Log.e(TAG, "로그인 실패 - ${response.error.message}")
                            _loginState.value = LoginStatus.Error("로그인에 실패했습니다: ${response.error.message}")
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "로그인 처리 중 예외 발생: ${e.message}")
                _loginState.value = LoginStatus.Error("서버와의 연결에 실패했습니다.")
            }
        }
    }

    fun resetLoginState() {
        _loginState.value = LoginStatus.Idle
    }

    //유저 회원가입 과정
    fun userJoin(userJoin: UserJoin?) {
        viewModelScope.launch {
            if (userJoin == null) {
                _signUpSuccess.value = false
                return@launch
            }
            try {
                signUpUseCase(
                    userJoin.birthYear,
                    userJoin.fingerprintRegistered,
                    userJoin.paymentPassword,
                    userJoin.userJob,
                    userJoin.accesstoken
                ).collect { response ->
                    when (response) {
                        is ResponseStatus.Success -> {
//                            Log.d("LoginViewModel", "jwt토큰 " + response.data.accessToken)
                            dataStore.saveJwtToken("Bearer " + response.data.accessToken)
                            _signUpSuccess.value = true
                        }
                        is ResponseStatus.Error -> {
//                            Log.d("LoginViewModel", "회원가입 실패: ${response.error.message}")
                            _signUpSuccess.value = false
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("LoginViewModel", "회원가입 중 예외 발생", e)
                _signUpSuccess.value = false
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

sealed class LoginStatus {
    object Idle : LoginStatus()
    object Success : LoginStatus()
    data class Error(val message: String) : LoginStatus()
}