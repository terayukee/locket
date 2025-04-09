package com.ssafy.locket.presentation.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "NotificationCheckViewModel"

@HiltViewModel
class NotificationCheckViewModel @Inject constructor(

) : ViewModel() {
    private val _isIconClicked = MutableSharedFlow<Boolean>(replay = 2, extraBufferCapacity = 1)
    val isIconClicked = _isIconClicked.asSharedFlow()

    fun setIconClicked() {
        viewModelScope.launch {
            _isIconClicked.emit(true)
        }
    }
}