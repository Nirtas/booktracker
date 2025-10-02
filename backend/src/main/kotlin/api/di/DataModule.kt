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
import ru.jerael.booktracker.backend.data.hasher.Argon2PasswordHasher
import ru.jerael.booktracker.backend.data.repository.*
import ru.jerael.booktracker.backend.data.service.EmailVerificationService
import ru.jerael.booktracker.backend.data.service.JwtService
import ru.jerael.booktracker.backend.data.service.OtpGeneratorImpl
import ru.jerael.booktracker.backend.data.storage.FileStorageImpl
import ru.jerael.booktracker.backend.data.storage.UserAssetStorageImpl
import ru.jerael.booktracker.backend.domain.config.JwtConfig
import ru.jerael.booktracker.backend.domain.config.OtpConfig
import ru.jerael.booktracker.backend.domain.hasher.PasswordHasher
import ru.jerael.booktracker.backend.domain.repository.*
import ru.jerael.booktracker.backend.domain.service.OtpGenerator
import ru.jerael.booktracker.backend.domain.service.TokenService
import ru.jerael.booktracker.backend.domain.service.VerificationService
import ru.jerael.booktracker.backend.domain.storage.FileStorage
import ru.jerael.booktracker.backend.domain.storage.UserAssetStorage

val dataModule = module {
    single<FileStorage> { FileStorageImpl(storagePath = get(Qualifiers.storagePath), logger = get()) }
    single<UserAssetStorage> { UserAssetStorageImpl(fileStorage = get(), imageBaseUrl = get(Qualifiers.imageBaseUrl)) }

    single<BookRepository> { BookRepositoryImpl() }
    single<GenreRepository> { GenreRepositoryImpl() }
    single<UserRepository> { UserRepositoryImpl() }
    single<VerificationRepository> { VerificationRepositoryImpl() }
    single<RefreshTokenRepository> { RefreshTokenRepositoryImpl() }

    single<PasswordHasher> { Argon2PasswordHasher() }

    single<VerificationService> {
        EmailVerificationService(
            verificationRepository = get(),
            otpGenerator = get(),
            smtpConfig = get(),
            otpValidityMinutes = get<OtpConfig>().validityMinutes
        )
    }
    single<OtpGenerator> { OtpGeneratorImpl(otpCodeLength = get<OtpConfig>().length) }
    single<TokenService> { JwtService(jwtConfig = get<JwtConfig>(), refreshTokenRepository = get()) }
}