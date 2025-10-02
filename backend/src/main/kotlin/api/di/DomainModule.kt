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
import ru.jerael.booktracker.backend.domain.usecases.book.*
import ru.jerael.booktracker.backend.domain.usecases.genre.GetGenresUseCase
import ru.jerael.booktracker.backend.domain.usecases.login.LoginUseCase
import ru.jerael.booktracker.backend.domain.usecases.token.RefreshTokenUseCase
import ru.jerael.booktracker.backend.domain.usecases.user.*
import ru.jerael.booktracker.backend.domain.usecases.verification.ResendVerificationCodeUseCase
import ru.jerael.booktracker.backend.domain.usecases.verification.VerifyCodeUseCase
import ru.jerael.booktracker.backend.domain.validation.validator.CoverValidator
import ru.jerael.booktracker.backend.domain.validation.validator.GenreValidator

val domainModule = module {
    single<GenreValidator> { GenreValidator(genreRepository = get()) }
    single<CoverValidator> { CoverValidator() }

    single<AddBookUseCase> {
        AddBookUseCase(
            bookRepository = get(),
            genreValidator = get(),
            userAssetStorage = get(),
            bookValidator = get(),
            coverValidator = get()
        )
    }
    single<GetBooksUseCase> { GetBooksUseCase(bookRepository = get()) }
    single<GetBookByIdUseCase> { GetBookByIdUseCase(bookRepository = get()) }
    single<UpdateBookDetailsUseCase> {
        UpdateBookDetailsUseCase(
            bookRepository = get(),
            bookValidator = get(),
            genreValidator = get()
        )
    }
    single<UpdateBookCoverUseCase> {
        UpdateBookCoverUseCase(
            bookRepository = get(),
            userAssetStorage = get(),
            coverValidator = get()
        )
    }
    single<DeleteBookUseCase> { DeleteBookUseCase(bookRepository = get(), userAssetStorage = get()) }

    single<GetGenresUseCase> { GetGenresUseCase(genreRepository = get()) }

    single<RegisterUserUseCase> {
        RegisterUserUseCase(
            userRepository = get(),
            passwordHasher = get(),
            verificationService = get(),
            userValidator = get()
        )
    }
    single<GetUserByIdUseCase> { GetUserByIdUseCase(userRepository = get()) }
    single<UpdateUserEmailUseCase> {
        UpdateUserEmailUseCase(
            userRepository = get(),
            passwordHasher = get(),
            verificationService = get(),
            userValidator = get()
        )
    }
    single<UpdateUserPasswordUseCase> {
        UpdateUserPasswordUseCase(
            userRepository = get(),
            passwordHasher = get(),
            userValidator = get()
        )
    }
    single<DeleteUserUseCase> {
        DeleteUserUseCase(
            userRepository = get(),
            passwordHasher = get(),
            userValidator = get(),
            userAssetStorage = get()
        )
    }

    single<LoginUseCase> {
        LoginUseCase(
            userRepository = get(),
            passwordHasher = get(),
            tokenService = get(),
            loginValidator = get()
        )
    }

    single<VerifyCodeUseCase> {
        VerifyCodeUseCase(
            userRepository = get(),
            verificationRepository = get(),
            tokenService = get(),
            verificationValidator = get()
        )
    }
    single<ResendVerificationCodeUseCase> {
        ResendVerificationCodeUseCase(
            userRepository = get(),
            verificationService = get(),
            verificationValidator = get()
        )
    }

    single<RefreshTokenUseCase> {
        RefreshTokenUseCase(
            userRepository = get(),
            refreshTokenRepository = get(),
            tokenService = get(),
            tokenValidator = get()
        )
    }
}