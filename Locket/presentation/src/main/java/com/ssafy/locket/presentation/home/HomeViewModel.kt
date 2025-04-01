package com.ssafy.locket.presentation.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.locket.data.network.response.user.UserInfoResponse
import com.ssafy.locket.repository.finance.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _user = MutableStateFlow<UserInfoResponse?>(null)
    val user: StateFlow<UserInfoResponse?> = _user.asStateFlow()

    fun fetchUser(userId: Int) {
        viewModelScope.launch {
            userRepository.getUserInfo(userId)
                .collect { response ->
                    if (response is ApiResponse.Success) {
                        _user.value = response.data // 성공 시 데이터 저장
                        Log.d("UserFragment", "User: ${response}")
                    } else {
                        // 실패 처리 (예: 에러 메시지 저장)
                        Log.e("UserViewModel", "Error fetching user: ${response.toString()}")
                    }
                }
        }
    }
}