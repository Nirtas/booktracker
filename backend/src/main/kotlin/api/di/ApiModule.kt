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

import org.koin.dsl.module
import ru.jerael.booktracker.backend.api.config.OtpConfig
import ru.jerael.booktracker.backend.api.controller.*
import ru.jerael.booktracker.backend.api.mappers.*
import ru.jerael.booktracker.backend.api.parsing.MultipartParser
import ru.jerael.booktracker.backend.api.validation.validator.BookValidator
import ru.jerael.booktracker.backend.api.validation.validator.LoginValidator
import ru.jerael.booktracker.backend.api.validation.validator.UserValidator
import ru.jerael.booktracker.backend.api.validation.validator.VerificationValidator

val apiModule = module {
    single<BookMapper> { BookMapperImpl(imageBaseUrl = get(qualifier = Qualifiers.imageBaseUrl), genreMapper = get()) }
    single<GenreMapper> { GenreMapperImpl() }
    single<UserMapper> { UserMapperImpl() }
    single<VerificationMapper> { VerificationMapperImpl() }
    single<LoginMapper> { LoginMapperImpl() }
    single<TokenMapper> { TokenMapperImpl() }

    single<MultipartParser> { MultipartParser() }
    single<BookValidator> { BookValidator() }
    single<UserValidator> { UserValidator() }
    single<VerificationValidator> { VerificationValidator(otpCodeLength = get<OtpConfig>().length) }
    single<LoginValidator> { LoginValidator() }

    single<BookController> {
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
    single<GenreController> {
        GenreController(
            getGenresUseCase = get(),
            genreMapper = get()
        )
    }
    single<UserController> {
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
    single<TokenController> {
        TokenController(
            loginUseCase = get(),
            loginValidator = get(),
            loginMapper = get(),
            tokenMapper = get()
        )
    }
    single<VerificationController> {
        VerificationController(
            verifyCodeUseCase = get(),
            resendVerificationCodeUseCase = get(),
            verificationValidator = get(),
            verificationMapper = get()
        )
    }
}