package com.ssafy.locket.model.home.character

data class CharacterInfo(
    val characterId: Int,
    val characterName: String,
    val createdAt: String,
    val exp: Int,
    val expPercentage: Double,
    val foodCount: Int,
    val level: Int,
    val totalExpForNextLevel: Int,
    val toy: Toy,
    val userId: Int
)