package com.ssafy.locket.data.di

import com.ssafy.locket.data.network.api.AuthService
import com.ssafy.locket.data.network.api.CharacterService
import com.ssafy.locket.data.network.api.UserService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module(includes = [NetworkModule::class, DataStoreModule::class])
@InstallIn(SingletonComponent::class)
internal class ApiModule {

    @Provides
    @Singleton
    fun provideCharacterService(
        @InterceptorRetrofit retrofit: Retrofit
    ): CharacterService {
        return retrofit.create(CharacterService::class.java)
    }

    @Provides
    @Singleton
    fun provideUserService(
        @InterceptorRetrofit retrofit: Retrofit
    ): UserService {
        return retrofit.create(UserService::class.java)
    }

    @Provides
    @Singleton
    fun provideAuthService(
        @NoInterceptorRetrofit retrofit: Retrofit
    ): AuthService {
        return retrofit.create(AuthService::class.java)
    }

//    @Provides
//    @Singleton
//    fun provideAnalysisService(retrofit: Retrofit): AnalysisService {
//        return retrofit.create(AnalysisService::class.java)
//    }
//
//    @Provides
//    @Singleton
//    fun provideBudgetService(retrofit: Retrofit): BudgetService {
//        return retrofit.create(BudgetService::class.java)
//    }
//
//    @Provides
//    @Singleton
//    fun providePaymentService(retrofit: Retrofit): PaymentService {
//        return retrofit.create(PaymentService::class.java)
//    }
//
//    @Provides
//    @Singleton
//    fun provideProductService(retrofit: Retrofit): ProductService {
//        return retrofit.create(ProductService::class.java)
//    }
//
//    @Provides
//    @Singleton
//    fun provideReceiptService(retrofit: Retrofit): ReceiptService {
//        return retrofit.create(ReceiptService::class.java)
//    }
//
//    @Provides
//    @Singleton
//    fun provideCharacterService(@BaseRetrofit retrofit: Retrofit): CharacterService {
//        return retrofit.create(CharacterService::class.java)
//    }

}