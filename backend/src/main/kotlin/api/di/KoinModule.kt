package ru.jerael.booktracker.backend.api.di

import io.ktor.server.application.*
import org.koin.dsl.module
import ru.jerael.booktracker.backend.data.repository.BookRepositoryImpl
import ru.jerael.booktracker.backend.data.storage.LocalFileStorage
import ru.jerael.booktracker.backend.domain.repository.BookRepository
import ru.jerael.booktracker.backend.domain.storage.FileStorage
import ru.jerael.booktracker.backend.domain.usecases.*

fun appModule(environment: ApplicationEnvironment) = module {
    single { environment }
    single<BookRepository> { BookRepositoryImpl() }
    single<FileStorage> { LocalFileStorage(get()) }

    single { GetBooksUseCase(get()) }
    single { AddBookUseCase(get()) }
    single { GetBookByIdUseCase(get()) }
    single { UpdateBookDetailsUseCase(get()) }
    single { UpdateBookCoverUseCase(get()) }
    single { DeleteBookUseCase(get(), get()) }
}