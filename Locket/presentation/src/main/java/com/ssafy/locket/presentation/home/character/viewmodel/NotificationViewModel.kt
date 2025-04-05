package com.ssafy.locket.presentation.home.character.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.locket.model.base.ResponseStatus
import com.ssafy.locket.model.home.Notification
import com.ssafy.locket.model.home.NotificationListInfo
import com.ssafy.locket.model.home.character.GifticonList
import com.ssafy.locket.presentation.graph.viewmodel.ProductCategoryListState
import com.ssafy.locket.usecase.character.GetAllGifticonsUseCase
import com.ssafy.locket.usecase.notification.NotificationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val notificationUseCase: NotificationUseCase
): ViewModel(){
    private var _notificationList = MutableStateFlow<NotificationState>(NotificationState.Initial)
    val notificationList: Flow<NotificationState> = _notificationList.asStateFlow()

    fun notificationSetLoading() {
        _notificationList.value = NotificationState.Loading
    }

    fun getNotifications() {
        viewModelScope.launch {
            notificationUseCase()
                .onStart {
                    notificationSetLoading()
                }
                .catch { e ->
                    Log.d("ProductFragment", "에러: " + e.message.toString())
                }
                .first()  // <- firstOrNull() 대신 first() 사용
                .let { uiState ->  // 항상 실행됨 (null이 될 수 없음)
                    when (uiState) {
                        is ResponseStatus.Success -> {
                            _notificationList.value = NotificationState.Success(uiState.data)
                            Log.d("ProductFragment", "Product: ${_notificationList.value}")
                        }
                        is ResponseStatus.Error -> {
                            _notificationList.value = NotificationState.Error(uiState.error.message)
                            Log.d("ProductFragment", "error: ${_notificationList.value}")
                        }
                    }
                }
        }
    }
}

sealed class NotificationState {
    object Initial: NotificationState()
    object Loading: NotificationState()
    data class Success(val notificationList: NotificationListInfo): NotificationState()
    data class Error(val message: String): NotificationState()
}