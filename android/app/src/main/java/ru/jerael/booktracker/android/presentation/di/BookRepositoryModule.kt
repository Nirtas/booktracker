package ru.jerael.booktracker.android.presentation.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.jerael.booktracker.android.data.repository.BookRepositoryImpl
import ru.jerael.booktracker.android.domain.repository.BookRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class BookRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindBookRepository(bookRepositoryImpl: BookRepositoryImpl): BookRepository
}