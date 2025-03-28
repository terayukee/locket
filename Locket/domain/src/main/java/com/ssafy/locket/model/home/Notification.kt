package com.ssafy.locket.model.home

data class Notification(
    val id: Int,
    val type: String,
    val title: String,
    val content: String,
    val date: String
)
