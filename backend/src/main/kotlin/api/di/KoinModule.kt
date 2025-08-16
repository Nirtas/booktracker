package ru.jerael.booktracker.backend.api.di

import io.ktor.server.application.*
import org.koin.dsl.module
import ru.jerael.booktracker.backend.api.controller.BookController
import ru.jerael.booktracker.backend.api.controller.GenreController
import ru.jerael.booktracker.backend.api.plugins.IMAGE_BASE_URL_PROPERTY
import ru.jerael.booktracker.backend.data.repository.BookRepositoryImpl
import ru.jerael.booktracker.backend.data.repository.GenreRepositoryImpl
import ru.jerael.booktracker.backend.data.storage.CoverStorageImpl
import ru.jerael.booktracker.backend.data.storage.FileStorageImpl
import ru.jerael.booktracker.backend.domain.repository.BookRepository
import ru.jerael.booktracker.backend.domain.repository.GenreRepository
import ru.jerael.booktracker.backend.domain.storage.CoverStorage
import ru.jerael.booktracker.backend.domain.storage.FileStorage
import ru.jerael.booktracker.backend.domain.usecases.book.*
import ru.jerael.booktracker.backend.domain.usecases.genre.GetGenresUseCase

fun appModule(environment: ApplicationEnvironment) = module {
    single { environment }
    single<FileStorage> { FileStorageImpl(get()) }
    single<CoverStorage> { CoverStorageImpl(get()) }

    single<BookRepository> { BookRepositoryImpl() }
    single<GenreRepository> { GenreRepositoryImpl() }

    single { GetBooksUseCase(get()) }
    single { AddBookUseCase(get(), get()) }
    single { GetBookByIdUseCase(get()) }
    single { UpdateBookDetailsUseCase(get()) }
    single { UpdateBookCoverUseCase(get(), get()) }
    single { DeleteBookUseCase(get(), get()) }
    single { GetGenresUseCase(get()) }

    single {
        BookController(
            getBooksUseCase = get(),
            addBookUseCase = get(),
            getBookByIdUseCase = get(),
            updateBookDetailsUseCase = get(),
            updateBookCoverUseCase = get(),
            deleteBookUseCase = get(),
            coverStorage = get(),
            imageBaseUrl = getProperty(IMAGE_BASE_URL_PROPERTY)
        )
    }

    single {
        GenreController(
            getGenresUseCase = get()
        )
    }
}