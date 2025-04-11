package com.ssafy.locket.data.di

import com.ssafy.locket.data.repository.finance.analysis.AnalysisRepositoryImpl
import android.content.Context
import com.ssafy.locket.data.repository.auth.AuthRepositoryImpl
import com.ssafy.locket.data.repository.finance.budget.BudgetRepositoryImpl
import com.ssafy.locket.data.repository.graph.ProductRepositoryImpl
import com.ssafy.locket.data.repository.home.character.CharacterRepositoryImpl
import com.ssafy.locket.data.repository.home.notification.NotificationRepositoryImpl
import com.ssafy.locket.data.repository.home.receipt.ReceiptFileRepositoryImpl
import com.ssafy.locket.data.repository.payment.PaymentRepositoryImpl
import com.ssafy.locket.data.repository.finance.payment_history.PaymentHistoryRepositoryImpl
import com.ssafy.locket.data.repository.user.DataStoreRepositoryImpl
import com.ssafy.locket.data.repository.user.UserRepositoryImpl
import com.ssafy.locket.repository.finance.analysis.AnalysisRepository
import com.ssafy.locket.repository.product.ProductRepository
import com.ssafy.locket.repository.auth.AuthRepository
import com.ssafy.locket.repository.finance.budget.BudgetRepository
import com.ssafy.locket.repository.user.UserRepository
import com.ssafy.locket.repository.home.character.CharacterRepository
import com.ssafy.locket.repository.notification.NotificationRepository
import com.ssafy.locket.repository.home.receipt.ReceiptFileRepository
import com.ssafy.locket.repository.payment.PaymentRepository
import com.ssafy.locket.repository.finance.payment_history.PaymentHistoryRepository
import com.ssafy.locket.repository.user.DataStoreRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module(includes = [NetworkModule::class, DataStoreModule::class])
@InstallIn(SingletonComponent::class)
internal abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindDataStoreRepository(
        dataStoreRepositoryImpl: DataStoreRepositoryImpl
    ): DataStoreRepository

    @Binds
    @Singleton
    abstract fun bindCharacterRepository(
        characterRepositoryImpl: CharacterRepositoryImpl
    ): CharacterRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(
        userRepositoryImpl: UserRepositoryImpl
    ): UserRepository

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindProductRepository(
        productRepositoryImpl: ProductRepositoryImpl
    ): ProductRepository

    @Binds
    @Singleton
    abstract fun bindPaymentRepository(
        paymentRepositoryImpl: PaymentRepositoryImpl
    ): PaymentRepository

    @Binds
    @Singleton
    abstract fun bindNotificationRepository(
        notificationRepositoryImpl: NotificationRepositoryImpl
    ): NotificationRepository

    @Binds
    @Singleton
    abstract fun bindPaymentHistoryRepository(
        paymentHistoryRepositoryImpl: PaymentHistoryRepositoryImpl
    ): PaymentHistoryRepository

    @Binds
    @Singleton
    abstract fun bindReceiptRepository(
        receiptFileRepositoryImpl: ReceiptFileRepositoryImpl
    ): ReceiptFileRepository

    @Binds
    @Singleton
    abstract fun bindBudgetRepository(
        budgetRepositoryImpl: BudgetRepositoryImpl
    ): BudgetRepository

    @Binds
    @Singleton
    abstract fun bindAnalysisRepository(
        analysisRepositoryImpl: AnalysisRepositoryImpl
    ): AnalysisRepository


}