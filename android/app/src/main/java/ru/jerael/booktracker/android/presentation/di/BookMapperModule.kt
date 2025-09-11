package ru.jerael.booktracker.android.presentation.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.jerael.booktracker.android.data.mappers.BookMapper
import ru.jerael.booktracker.android.data.mappers.BookMapperImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class BookMapperModule {

    @Binds
    @Singleton
    abstract fun bindBookMapper(bookMapperImpl: BookMapperImpl): BookMapper
}