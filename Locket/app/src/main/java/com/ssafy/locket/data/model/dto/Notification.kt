package com.ssafy.locket.data.model.dto

data class Notification(
    val id: Int,
    val type: String,
    val title: String,
    val content: String,
    val date: String
)
