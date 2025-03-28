package com.ssafy.locket.data.di

import com.ssafy.locket.data.repository.finance.FinanceRepositoryImpl
import com.ssafy.locket.repository.finance.FinanceRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun binFinanceRepository(
        financeRepositoryImpl: FinanceRepositoryImpl
    ): FinanceRepository
}