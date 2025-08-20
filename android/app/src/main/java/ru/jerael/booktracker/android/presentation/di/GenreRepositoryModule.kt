package ru.jerael.booktracker.android.presentation.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.jerael.booktracker.android.data.repository.GenreRepositoryImpl
import ru.jerael.booktracker.android.domain.repository.GenreRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class GenreRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindGenreRepository(genreRepositoryImpl: GenreRepositoryImpl): GenreRepository
}