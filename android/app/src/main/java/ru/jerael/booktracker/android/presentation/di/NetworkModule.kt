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

package ru.jerael.booktracker.android.presentation.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.ANDROID
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import ru.jerael.booktracker.android.BuildConfig
import ru.jerael.booktracker.android.data.remote.api.BookApiService
import ru.jerael.booktracker.android.data.remote.api.BookApiServiceImpl
import ru.jerael.booktracker.android.data.remote.api.GenreApiService
import ru.jerael.booktracker.android.data.remote.api.GenreApiServiceImpl
import java.util.Locale
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideHttpClient(): HttpClient {
        return HttpClient(CIO) {
            install(ContentNegotiation) {
                json()
            }
            install(Logging) {
                level = LogLevel.ALL
                logger = Logger.ANDROID
            }
            defaultRequest {
                url(urlString = BuildConfig.BACKEND_BASE_URL)
                header(HttpHeaders.ContentType, ContentType.Application.Json)
                val languageTag = Locale.getDefault().toLanguageTag()
                header(HttpHeaders.AcceptLanguage, languageTag)
            }
        }
    }

    @Provides
    @Singleton
    fun provideBookApiService(httpClient: HttpClient): BookApiService {
        return BookApiServiceImpl(httpClient)
    }

    @Provides
    @Singleton
    fun provideGenreApiService(httpClient: HttpClient): GenreApiService {
        return GenreApiServiceImpl(httpClient)
    }
}