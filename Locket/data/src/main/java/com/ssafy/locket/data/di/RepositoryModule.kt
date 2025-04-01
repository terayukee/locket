package com.ssafy.locket.data.di

import com.ssafy.locket.data.network.api.CharacterService
import com.ssafy.locket.data.repository.home.character.CharacterRepositoryImpl
import com.ssafy.locket.repository.home.character.CharacterRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module(includes = [NetworkModule::class, DataStoreModule::class])
@InstallIn(SingletonComponent::class)
internal class RepositoryModule {
    @Singleton
    @Provides
    fun provideCharacterRepository(
        characterService: CharacterService,
    ): CharacterRepository {
        return CharacterRepositoryImpl(characterService)
    }
}