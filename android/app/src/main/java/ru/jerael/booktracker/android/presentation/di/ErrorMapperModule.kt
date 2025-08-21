package ru.jerael.booktracker.android.presentation.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.jerael.booktracker.android.data.mappers.ErrorMapperImpl
import ru.jerael.booktracker.android.domain.mappers.ErrorMapper
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ErrorMapperModule {

    @Binds
    @Singleton
    abstract fun bindErrorMapper(errorMapperImpl: ErrorMapperImpl): ErrorMapper
}