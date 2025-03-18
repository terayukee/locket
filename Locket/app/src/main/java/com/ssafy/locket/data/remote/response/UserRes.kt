package com.ssafy.locket.data.remote.response

data class UserRes(
    val birth_date: String,
    val created_at: String,
    val email: String,
    val login_id: String,
    val nickname: String,
    val user_id: Int
)