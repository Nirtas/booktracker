package ru.jerael.booktracker.android.presentation.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.jerael.booktracker.android.data.mappers.GenreMapper
import ru.jerael.booktracker.android.data.mappers.GenreMapperImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class GenreMapperModule {

    @Binds
    @Singleton
    abstract fun bindGenreMapper(genreMapperImpl: GenreMapperImpl): GenreMapper
}