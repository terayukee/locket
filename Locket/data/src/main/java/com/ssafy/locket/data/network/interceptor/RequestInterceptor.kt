package com.ssafy.locket.data.network.interceptor

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.ssafy.locket.data.datasource.local.UserDataStoreSource
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class RequestInterceptor constructor(private val dataStore: UserDataStoreSource): Interceptor {
    companion object {
        private val ACCESS_TOKEN = stringPreferencesKey("access_token")
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val token = runBlocking {
            dataStore.accessToken.first() ?: ""
        }
        val requestWithToken = chain.request().newBuilder()
            .addHeader("Authorization", token)
            .build()

        return chain.proceed(requestWithToken)
    }
}