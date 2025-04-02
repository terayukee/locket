package com.ssafy.locket.data.datasource.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.google.gson.Gson
import com.ssafy.locket.model.user.UserInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserDataStoreSource @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        val ACCESS_TOKEN = stringPreferencesKey("access_token")
        val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        val NICKNAME = stringPreferencesKey("nickname")
        val USER_ID = longPreferencesKey("user_id")
        val FCM_TOKEN = stringPreferencesKey("fcm_token")
        val JWT_TOKEN =  stringPreferencesKey("jwt_token")
        val USER_OBJECT = stringPreferencesKey("user_object")
    }

    private val gson = Gson()


    suspend fun saveAccessToken(accessToken: String) {
        dataStore.edit { preferences ->
            preferences[ACCESS_TOKEN] = accessToken
        }
    }

    suspend fun saveFcmToken(fcmToken: String){
        dataStore.edit{preferences ->
            preferences[FCM_TOKEN] = fcmToken
        }
    }

    suspend fun saveRefreshToken(refreshToken: String) {
        dataStore.edit { preferences ->
            preferences[REFRESH_TOKEN] = refreshToken
        }
    }

    suspend fun saveNickname(nickname: String) {
        dataStore.edit { preferences ->
            preferences[NICKNAME] = nickname
        }
    }

    suspend fun saveUserId(userId: Long) {
        dataStore.edit { preferences ->
            preferences[USER_ID] = userId
        }
    }

    suspend fun saveJwtToken(jwtToken: String) {
        dataStore.edit { preferences ->
            preferences[JWT_TOKEN] = jwtToken
        }
    }

    suspend fun saveUser(user: UserInfo) {
        val userJson = gson.toJson(user)
        dataStore.edit { preferences ->
            preferences[USER_OBJECT] = userJson
        }
    }

    val accessToken: Flow<String?> = dataStore.data.map { preferences ->
        preferences[ACCESS_TOKEN]
    }

    val refreshToken: Flow<String?> = dataStore.data.map { preferences ->
        preferences[REFRESH_TOKEN]
    }

    val nickname: Flow<String?> = dataStore.data.map { preferences ->
        preferences[NICKNAME]
    }

    val userId: Flow<Long?> = dataStore.data.map { preferences ->
        preferences[USER_ID]
    }

    val fcmToken : Flow<String?> = dataStore.data.map{preferences->
        preferences[FCM_TOKEN]
    }

    val jwtToken : Flow<String?> = dataStore.data.map{preferences->
        preferences[JWT_TOKEN]
    }

    val user: Flow<UserInfo?> = dataStore.data.map { preferences ->
        val userJson = preferences[USER_OBJECT]
        userJson?.let { gson.fromJson(it, UserInfo::class.java) }
    }

    suspend fun clearAll() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}