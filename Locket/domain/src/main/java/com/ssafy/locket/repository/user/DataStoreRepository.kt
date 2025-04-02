package com.ssafy.locket.repository.user

import kotlinx.coroutines.flow.Flow

interface DataStoreRepository {
    val accessToken: Flow<String?>
    suspend fun saveAccessToken(token: String)

    val refreshToken: Flow<String?>
    suspend fun saveRefreshToken(token: String)

    val nickname: Flow<String?>
    suspend fun saveNickname(nickname: String)

    val userId: Flow<Long?>
    suspend fun saveUserId(userId: Long)

    val fcmToken: Flow<String?>
    suspend fun saveFcmToken(fcmToken: String)

    suspend fun clearAll()
}