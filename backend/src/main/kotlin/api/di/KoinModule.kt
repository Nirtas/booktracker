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
import io.ktor.server.config.*
import org.koin.dsl.module
import ru.jerael.booktracker.backend.api.config.SmtpConfig
import ru.jerael.booktracker.backend.api.config.smtpConfig
import ru.jerael.booktracker.backend.api.controller.*
import ru.jerael.booktracker.backend.api.mappers.*
import ru.jerael.booktracker.backend.api.parsing.MultipartParser
import ru.jerael.booktracker.backend.api.plugins.IMAGE_BASE_URL_PROPERTY
import ru.jerael.booktracker.backend.api.plugins.KTOR_APPLICATION_CONFIG_PROPERTY
import ru.jerael.booktracker.backend.api.validation.validator.BookValidator
import ru.jerael.booktracker.backend.api.validation.validator.LoginValidator
import ru.jerael.booktracker.backend.api.validation.validator.UserValidator
import ru.jerael.booktracker.backend.api.validation.validator.VerificationValidator
import ru.jerael.booktracker.backend.data.repository.BookRepositoryImpl
import ru.jerael.booktracker.backend.data.repository.GenreRepositoryImpl
import ru.jerael.booktracker.backend.data.repository.UserRepositoryImpl
import ru.jerael.booktracker.backend.data.repository.VerificationRepositoryImpl
import ru.jerael.booktracker.backend.data.storage.CoverStorageImpl
import ru.jerael.booktracker.backend.data.storage.FileStorageImpl
import ru.jerael.booktracker.backend.domain.hasher.Argon2PasswordHasher
import ru.jerael.booktracker.backend.domain.hasher.PasswordHasher
import ru.jerael.booktracker.backend.domain.repository.BookRepository
import ru.jerael.booktracker.backend.domain.repository.GenreRepository
import ru.jerael.booktracker.backend.domain.repository.UserRepository
import ru.jerael.booktracker.backend.domain.repository.VerificationRepository
import ru.jerael.booktracker.backend.domain.service.EmailVerificationService
import ru.jerael.booktracker.backend.domain.service.TempTokenService
import ru.jerael.booktracker.backend.domain.service.TokenService
import ru.jerael.booktracker.backend.domain.service.VerificationService
import ru.jerael.booktracker.backend.domain.storage.CoverStorage
import ru.jerael.booktracker.backend.domain.storage.FileStorage
import ru.jerael.booktracker.backend.domain.usecases.GenresValidator
import ru.jerael.booktracker.backend.domain.usecases.book.*
import ru.jerael.booktracker.backend.domain.usecases.genre.GetGenresUseCase
import ru.jerael.booktracker.backend.domain.usecases.login.LoginUseCase
import ru.jerael.booktracker.backend.domain.usecases.user.*
import ru.jerael.booktracker.backend.domain.usecases.verification.ResendVerificationCodeUseCase
import ru.jerael.booktracker.backend.domain.usecases.verification.VerifyCodeUseCase

fun appModule(environment: ApplicationEnvironment) = module {
    single { environment }
    single<FileStorage> { FileStorageImpl(environment = get()) }
    single<CoverStorage> { CoverStorageImpl(fileStorage = get()) }

    single<BookRepository> { BookRepositoryImpl() }
    single<GenreRepository> { GenreRepositoryImpl() }
    single<UserRepository> { UserRepositoryImpl() }
    single<VerificationRepository> { VerificationRepositoryImpl() }

    single<GenresValidator> { GenresValidator(genreRepository = get()) }

    single<BookMapper> { BookMapperImpl(imageBaseUrl = getProperty(IMAGE_BASE_URL_PROPERTY), genreMapper = get()) }
    single<GenreMapper> { GenreMapperImpl() }
    single<UserMapper> { UserMapperImpl() }
    single<VerificationMapper> { VerificationMapperImpl() }
    single<LoginMapper> { LoginMapperImpl() }
    single<TokenMapper> { TokenMapperImpl() }

    single { MultipartParser() }
    single { BookValidator() }
    single { UserValidator() }
    single { VerificationValidator() }
    single { LoginValidator() }

    single<PasswordHasher> { Argon2PasswordHasher() }
    single<SmtpConfig> { getProperty<ApplicationConfig>(KTOR_APPLICATION_CONFIG_PROPERTY).smtpConfig() }
    single<VerificationService> { EmailVerificationService(verificationRepository = get(), smtpConfig = get()) }
    single<TokenService> { TempTokenService() }

    single { GetBooksUseCase(bookRepository = get()) }
    single { AddBookUseCase(bookRepository = get(), genresValidator = get(), coverStorage = get()) }
    single { GetBookByIdUseCase(bookRepository = get()) }
    single { UpdateBookDetailsUseCase(bookRepository = get(), genresValidator = get()) }
    single { UpdateBookCoverUseCase(bookRepository = get(), coverStorage = get()) }
    single { DeleteBookUseCase(bookRepository = get(), fileStorage = get()) }
    single { GetGenresUseCase(genreRepository = get()) }
    single { RegisterUserUseCase(userRepository = get(), passwordHasher = get(), verificationService = get()) }
    single { GetUserByIdUseCase(userRepository = get()) }
    single { UpdateUserEmailUseCase(userRepository = get(), passwordHasher = get(), verificationService = get()) }
    single { UpdateUserPasswordUseCase(userRepository = get(), passwordHasher = get()) }
    single { DeleteUserUseCase(userRepository = get(), passwordHasher = get()) }
    single { VerifyCodeUseCase(userRepository = get(), verificationRepository = get()) }
    single { LoginUseCase(userRepository = get(), passwordHasher = get(), tokenService = get()) }
    single { ResendVerificationCodeUseCase(userRepository = get(), verificationService = get()) }

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

    single {
        UserController(
            registerUserUseCase = get(),
            getUserByIdUseCase = get(),
            updateUserEmailUseCase = get(),
            updateUserPasswordUseCase = get(),
            deleteUserUseCase = get(),
            userValidator = get(),
            userMapper = get()
        )
    }
    single {
        TokenController(
            loginUseCase = get(),
            loginValidator = get(),
            loginMapper = get(),
            tokenMapper = get()
        )
    }
    single {
        VerificationController(
            verifyCodeUseCase = get(),
            resendVerificationCodeUseCase = get(),
            verificationValidator = get(),
            verificationMapper = get()
        )
    }
}