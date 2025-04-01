package com.ssafy.locket.data.di

import dagger.Module
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.ssafy.locket.data.BuildConfig
import com.ssafy.locket.data.datasource.local.UserDataStoreSource
import com.ssafy.locket.data.network.interceptor.RequestInterceptor
import com.ssafy.locket.data.network.logger.LocketApiLogger
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    private const val SERVER_URL = "http://192.168.137.132:53382/api/"
//    private const val SERVER_URL = BuildConfig.BASE_URL // 찐서버 url

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder()
            .setDateFormat("yyyy-MM-dd HH:mm:ss")
            .setLenient()
            .create()
    }

    @InterceptorOkHttpClient
    @Singleton
    @Provides
    fun provideInterceptorOkHttp(requestInterceptor: RequestInterceptor): OkHttpClient {
        return OkHttpClient.Builder().apply {
            connectTimeout(10, TimeUnit.SECONDS)
            readTimeout(10, TimeUnit.SECONDS)
            writeTimeout(10, TimeUnit.SECONDS)
            addInterceptor(requestInterceptor)
            addInterceptor(
                HttpLoggingInterceptor(LocketApiLogger())
                    .apply { setLevel(HttpLoggingInterceptor.Level.BODY) }
            )
        }.build()
    }

    @NoInterceptorOkHttpClient
    @Singleton
    @Provides
    fun provideOkHttp(): OkHttpClient {
        return OkHttpClient.Builder().apply {
            connectTimeout(10, TimeUnit.SECONDS)
            readTimeout(10, TimeUnit.SECONDS)
            writeTimeout(10, TimeUnit.SECONDS)
            addInterceptor(
                HttpLoggingInterceptor(LocketApiLogger())
                    .apply { setLevel(HttpLoggingInterceptor.Level.BODY) }
            )
        }.build()
    }

    @InterceptorRetrofit
    @Provides
    @Singleton
    fun provideInterceptorRetrofit(@InterceptorOkHttpClient okHttpClient: OkHttpClient, gson: Gson): Retrofit {
        return Retrofit.Builder()
            .baseUrl(SERVER_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(okHttpClient)
            .build()
    }

    @NoInterceptorRetrofit
    @Provides
    @Singleton
    fun provideRetrofit(@NoInterceptorOkHttpClient okHttpClient: OkHttpClient, gson: Gson): Retrofit {
        return Retrofit.Builder()
            .baseUrl(SERVER_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(okHttpClient)
            .build()
    }

    @Provides
    fun provideRequestInterceptor(userDataStoreSource: UserDataStoreSource): RequestInterceptor {
        return RequestInterceptor(userDataStoreSource)
    }
}