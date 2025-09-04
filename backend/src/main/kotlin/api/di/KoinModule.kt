package ru.jerael.booktracker.backend.api.di

import io.ktor.server.application.*
import org.koin.dsl.module
import ru.jerael.booktracker.backend.api.controller.BookController
import ru.jerael.booktracker.backend.api.controller.GenreController
import ru.jerael.booktracker.backend.api.mappers.BookMapper
import ru.jerael.booktracker.backend.api.mappers.BookMapperImpl
import ru.jerael.booktracker.backend.api.mappers.GenreMapper
import ru.jerael.booktracker.backend.api.mappers.GenreMapperImpl
import ru.jerael.booktracker.backend.api.parsing.MultipartParser
import ru.jerael.booktracker.backend.api.plugins.IMAGE_BASE_URL_PROPERTY
import ru.jerael.booktracker.backend.api.validation.BookValidator
import ru.jerael.booktracker.backend.data.repository.BookRepositoryImpl
import ru.jerael.booktracker.backend.data.repository.GenreRepositoryImpl
import ru.jerael.booktracker.backend.data.storage.CoverStorageImpl
import ru.jerael.booktracker.backend.data.storage.FileStorageImpl
import ru.jerael.booktracker.backend.domain.repository.BookRepository
import ru.jerael.booktracker.backend.domain.repository.GenreRepository
import ru.jerael.booktracker.backend.domain.storage.CoverStorage
import ru.jerael.booktracker.backend.domain.storage.FileStorage
import ru.jerael.booktracker.backend.domain.usecases.GenresValidator
import ru.jerael.booktracker.backend.domain.usecases.book.*
import ru.jerael.booktracker.backend.domain.usecases.genre.GetGenresUseCase

fun appModule(environment: ApplicationEnvironment) = module {
    single { environment }
    single<FileStorage> { FileStorageImpl(get()) }
    single<CoverStorage> { CoverStorageImpl(get()) }

    single<BookRepository> { BookRepositoryImpl() }
    single<GenreRepository> { GenreRepositoryImpl() }

    single<GenresValidator> { GenresValidator(get()) }

    single<BookMapper> { BookMapperImpl(getProperty(IMAGE_BASE_URL_PROPERTY)) }
    single<GenreMapper> { GenreMapperImpl() }

    single { MultipartParser() }
    single { BookValidator() }

    single { GetBooksUseCase(get()) }
    single { AddBookUseCase(get(), get(), get()) }
    single { GetBookByIdUseCase(get()) }
    single { UpdateBookDetailsUseCase(get(), get(), get()) }
    single { UpdateBookCoverUseCase(get(), get(), get()) }
    single { DeleteBookUseCase(get(), get(), get()) }
    single { GetGenresUseCase(get()) }

    single {
        BookController(
            getBooksUseCase = get(),
            addBookUseCase = get(),
            getBookByIdUseCase = get(),
            updateBookDetailsUseCase = get(),
            updateBookCoverUseCase = get(),
            deleteBookUseCase = get(),
            validator = get(),
            multipartParser = get(),
            bookMapper = get()
        )
    }

    single {
        GenreController(
            getGenresUseCase = get(),
            genreMapper = get()
        )
    }
}