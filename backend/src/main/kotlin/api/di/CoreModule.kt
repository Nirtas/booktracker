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
import io.ktor.util.logging.*
import org.koin.core.qualifier.named
import org.koin.dsl.module
import ru.jerael.booktracker.backend.api.config.jwtConfig
import ru.jerael.booktracker.backend.api.config.otpConfig
import ru.jerael.booktracker.backend.api.config.smtpConfig
import ru.jerael.booktracker.backend.domain.config.JwtConfig
import ru.jerael.booktracker.backend.domain.config.OtpConfig
import ru.jerael.booktracker.backend.domain.config.SmtpConfig

object Qualifiers {
    val imageBaseUrl = named("imageBaseUrl")
    val storagePath = named("storagePath")
}

fun coreModule(application: Application) = module {
    single<Logger> { application.log }
    single(qualifier = Qualifiers.imageBaseUrl) {
        application.environment.config.property("ktor.storage.baseUrl").getString()
    }
    single(qualifier = Qualifiers.storagePath) {
        application.environment.config.property("ktor.storage.persistentPath").getString()
    }
    single<SmtpConfig> { application.environment.config.smtpConfig() }
    single<OtpConfig> { application.environment.config.otpConfig() }
    single<JwtConfig> { application.environment.config.jwtConfig() }
}