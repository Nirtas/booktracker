package ru.jerael.booktracker.backend.api.di

import org.koin.dsl.module
import ru.jerael.booktracker.backend.data.repository.BookRepositoryImpl
import ru.jerael.booktracker.backend.domain.repository.BookRepository
import ru.jerael.booktracker.backend.domain.usecases.GetBooksUseCase

val appModule = module {
    single<BookRepository> { BookRepositoryImpl() }
    single { GetBooksUseCase(get()) }
}