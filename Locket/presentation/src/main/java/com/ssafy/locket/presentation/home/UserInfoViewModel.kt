package com.ssafy.locket.presentation.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.locket.model.base.ResponseStatus
import com.ssafy.locket.model.user.UserInfo
import com.ssafy.locket.usecase.user.DeleteUserInfoUseCase
import com.ssafy.locket.usecase.user.GetUserInfoUseCase
import com.ssafy.locket.usecase.user.UpdateUserInfoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserInfoViewModel @Inject constructor(
    private val getUserInfoUseCase: GetUserInfoUseCase,
    private val updateUserInfoUseCase: UpdateUserInfoUseCase,
    private val deleteUserInfoUseCase: DeleteUserInfoUseCase
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
                .firstOrNull()
                .let { uiState ->
                    when(uiState) {
                        is ResponseStatus.Success -> {
                            _userInfo.value = UserInfoState.Success(uiState.data)
                            Log.d("UserFragment", "User: ${_userInfo.value}")
                        }
                        is ResponseStatus.Error -> {
                            _userInfo.value = UserInfoState.Error(uiState.error.message)
                            Log.d("UserFragment", "fetchUser: ${_userInfo.value}")
                        }
                        else -> Log.d("UserFragment", "fetchUser: else error")
                    }
                }
        }
    }

    fun updateUser(userId: Long,userInfo: UserInfo) {
        viewModelScope.launch {
            updateUserInfoUseCase(userId,userInfo)
                .onStart { setLoading() }
                .catch { e ->
                    Log.e("UserFragment", "에러 발생: ${e.message}", e)
                }
                .first()
                .let { uiState ->
                    when(uiState) {
                        is ResponseStatus.Success -> {
                            _userInfo.value = UserInfoState.Success(uiState.data)
                            Log.d("UserFragment", "User sdf: ${_userInfo.value}")
                        }
                        is ResponseStatus.Error -> {
                            _userInfo.value = UserInfoState.Error(uiState.error.message)
                            Log.d("UserFragment", "updateUser: ${_userInfo.value}")
                        }
                    }
                }
        }
    }

    fun deleteUser(userId: Long) {
        viewModelScope.launch {
            deleteUserInfoUseCase(userId)
                .onStart { setLoading() }
                .catch { e ->
                    Log.e("UserFragment", "에러 발생: ${e.message}", e)
                }
                .first() // ✅ collect 대신 first() 사용
                .let { uiState ->
                    when (uiState) {
                        is ResponseStatus.Success -> {
                            _userInfo.value = UserInfoState.Initial
                            Log.d("UserFragment", "User sdf: ${_userInfo.value}")
                        }
                        is ResponseStatus.Error -> {
                            _userInfo.value = UserInfoState.Error(uiState.error.message)
                            Log.d("UserFragment", "updateUser: ${_userInfo.value}")
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