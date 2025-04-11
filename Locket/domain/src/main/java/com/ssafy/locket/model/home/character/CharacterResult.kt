package com.ssafy.locket.model.home.character

sealed class CharacterResult {
    data class Exist(val info: CharacterInfo) : CharacterResult()
    data class NotExist(val status: CharacterStatus) : CharacterResult()
    data class Error(val message: String) : CharacterResult()
}