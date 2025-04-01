package com.ssafy.locket.data.di

import com.ssafy.locket.data.repository.finance.FinanceRepositoryImpl
import com.ssafy.locket.data.repository.finance.UserRepositoryImpl
import com.ssafy.locket.repository.finance.FinanceRepository
import com.ssafy.locket.repository.finance.UserRepository
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

    @Binds
    abstract fun bindUserRepository(userRepositoryImpl: UserRepositoryImpl): UserRepository
}