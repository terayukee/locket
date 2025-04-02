package com.ssafy.locket.presentation.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.locket.model.base.ResponseStatus
import com.ssafy.locket.model.user.UserInfo
import com.ssafy.locket.usecase.user.GetUserInfoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserInfoViewModel @Inject constructor(
    private val getUserInfoUseCase: GetUserInfoUseCase
) : ViewModel() {

    private val _userInfo = MutableStateFlow<UserInfoState>(UserInfoState.Initial)
    val userInfo: StateFlow<UserInfoState> = _userInfo.asStateFlow()

    fun setLoading() {
        _userInfo.value = UserInfoState.Loading
    }

    fun fetchUser(userId: Long) {
        viewModelScope.launch {
            getUserInfoUseCase(userId)
                .onStart { setLoading() }
                .catch { e ->

                }
                .collect { uiState ->
                    when(uiState) {
                        is ResponseStatus.Success -> {
                            _userInfo.value = UserInfoState.Success(uiState.data)
                            Log.d("UserFragment", "User: ${_userInfo.value}")
                        }
                        is ResponseStatus.Error -> {
                            _userInfo.value = UserInfoState.Error(uiState.error.message)
                            Log.d("UserFragment", "fetchUser: ${_userInfo.value}")
                        }
                    }
                }
        }
    }







}

sealed class UserInfoState {
    object Initial: UserInfoState()
    object Loading: UserInfoState()
    data class Success(val userInfo: UserInfo): UserInfoState()
    data class Error(val message: String): UserInfoState()
}