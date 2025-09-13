/*
 * BookTracker is a full-stack application for tracking your reading list.
 * Copyright (C) 2025  Jerael (https://github.com/Nirtas)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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

    single<BookMapper> { BookMapperImpl(getProperty(IMAGE_BASE_URL_PROPERTY), get()) }
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