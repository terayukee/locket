package com.ssafy.locket.presentation.login.register_user_info

import android.view.View
import com.ssafy.locket.R

class PasswordInputHandler(
    private val passwordDots: Array<View>,
    private val maxPasswordLength: Int,
    private val onPasswordComplete: () -> Unit
) {
    private val password = StringBuilder()

    fun onNumberClicked(number: String) {
        if (password.length < maxPasswordLength) {
            password.append(number)
            updatePasswordDots()
            if (password.length == maxPasswordLength) {
                onPasswordComplete()  // 비밀번호 완료 시 호출
            }
        }
    }

    fun removeLastDigit() {
        if (password.isNotEmpty()) {
            password.deleteCharAt(password.length - 1)
            updatePasswordDots()
        }
    }

    private fun updatePasswordDots() {
        for (i in passwordDots.indices) {
            passwordDots[i].setBackgroundResource(R.drawable.bg_password_dot)
        }
        for (i in 0 until password.length) {
            passwordDots[i].setBackgroundResource(R.drawable.bg_password_input_dot)
        }
    }

    // 비밀번호 초기화
    fun clearPassword() {
        password.clear()
        updatePasswordDots()
    }

    fun getPassword(): StringBuilder{
        return password
    }
}