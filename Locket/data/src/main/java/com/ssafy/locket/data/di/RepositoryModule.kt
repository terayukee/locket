package com.ssafy.locket.data.di

import android.app.Activity
import com.ssafy.locket.data.repository.finance.FinanceRepositoryImpl
import com.ssafy.locket.repository.finance.FinanceRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(ActivityComponent::class)
abstract class RepositoryModule {

}