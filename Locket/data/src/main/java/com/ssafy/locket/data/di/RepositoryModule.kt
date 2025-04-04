package com.ssafy.locket.data.di

import com.ssafy.locket.data.repository.auth.AuthRepositoryImpl
import com.ssafy.locket.data.repository.graph.ProductRepositoryImpl
import com.ssafy.locket.data.repository.home.character.CharacterRepositoryImpl
import com.ssafy.locket.data.repository.user.DataStoreRepositoryImpl
import com.ssafy.locket.data.repository.user.UserRepositoryImpl
import com.ssafy.locket.repository.Product.ProductRepository
import com.ssafy.locket.repository.auth.AuthRepository
import com.ssafy.locket.repository.user.UserRepository
import com.ssafy.locket.repository.home.character.CharacterRepository
import com.ssafy.locket.repository.user.DataStoreRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
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
}