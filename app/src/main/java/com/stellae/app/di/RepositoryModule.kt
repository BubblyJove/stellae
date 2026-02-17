package com.stellae.app.di

import com.stellae.app.data.repository.CardRepositoryImpl
import com.stellae.app.data.repository.DignityRepositoryImpl
import com.stellae.app.data.repository.UserProgressRepositoryImpl
import com.stellae.app.domain.repository.CardRepository
import com.stellae.app.domain.repository.DignityRepository
import com.stellae.app.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module that binds repository implementation classes to their
 * corresponding interfaces so the domain layer never depends on the
 * data layer directly.
 *
 * All bindings are singleton-scoped: one instance per application process.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindCardRepository(
        impl: CardRepositoryImpl,
    ): CardRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(
        impl: UserProgressRepositoryImpl,
    ): UserRepository

    @Binds
    @Singleton
    abstract fun bindDignityRepository(
        impl: DignityRepositoryImpl,
    ): DignityRepository
}
