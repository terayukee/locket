package com.ssafy.locket.model.home.character

data class Toy(
    val isAvailable: Boolean,
    val nextAvailableTime: Any,
    val remainingTimeMinutes: Int
)