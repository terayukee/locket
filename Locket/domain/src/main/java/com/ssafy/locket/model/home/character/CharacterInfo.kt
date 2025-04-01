package com.ssafy.locket.model.home.character

import com.ssafy.locket.model.base.BaseModel
import kotlinx.parcelize.Parcelize

@Parcelize
class CharacterInfo(
    val id: Long,
    val name: String,
    val exp: Int,
    val expPercentage: Double,
    val foodCount: Int,
    val level: Int,
    val totalExpForNextLevel: Int,
    val toy: Toy
): BaseModel