package com.ssafy.locket.di

import com.ssafy.locket.data.network.api.AnalysisService
import com.ssafy.locket.data.network.api.BudgetService
import com.ssafy.locket.data.network.api.PaymentService
import com.ssafy.locket.data.network.api.ProductService
import com.ssafy.locket.data.network.api.ReceiptService
import com.ssafy.locket.data.network.api.UserService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object ApiModule {
    @Provides
    @Singleton
    fun provideAnalysisService(retrofit: Retrofit): AnalysisService {
        return retrofit.create(AnalysisService::class.java)
    }

    @Provides
    @Singleton
    fun provideBudgetService(retrofit: Retrofit): BudgetService {
        return retrofit.create(BudgetService::class.java)
    }

    @Provides
    @Singleton
    fun providePaymentService(retrofit: Retrofit): PaymentService {
        return retrofit.create(PaymentService::class.java)
    }

    @Provides
    @Singleton
    fun provideProductService(retrofit: Retrofit): ProductService {
        return retrofit.create(ProductService::class.java)
    }

    @Provides
    @Singleton
    fun provideReceiptService(retrofit: Retrofit): ReceiptService {
        return retrofit.create(ReceiptService::class.java)
    }

    @Provides
    @Singleton
    fun provideUserService(retrofit: Retrofit): UserService {
        return retrofit.create(UserService::class.java)
    }
}