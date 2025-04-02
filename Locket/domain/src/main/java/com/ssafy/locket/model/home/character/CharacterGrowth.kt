package com.ssafy.locket.model.home.character

import com.ssafy.locket.model.base.BaseModel
import kotlinx.parcelize.Parcelize

@Parcelize
data class CharacterGrowth(
    val name: String,
    val currentExp: Int,
    val expPercentage: Double,
    val level: Int,
    val levelUp: Boolean,
    val minRemain: Int,
    val toyAvailable: Boolean
): BaseModel