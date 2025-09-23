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

package api.routes.book

import io.mockk.MockKAnnotations
import io.mockk.clearAllMocks
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.mockk
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestInstance
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import ru.jerael.booktracker.backend.api.controller.*
import ru.jerael.booktracker.backend.api.mappers.BookMapperImpl
import ru.jerael.booktracker.backend.api.mappers.GenreMapperImpl
import ru.jerael.booktracker.backend.api.parsing.MultipartParser
import ru.jerael.booktracker.backend.api.validation.validator.BookValidator
import ru.jerael.booktracker.backend.domain.usecases.book.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class BooksRouteTestBase : KoinTest {

    @MockK
    protected lateinit var getBooksUseCase: GetBooksUseCase

    @MockK
    protected lateinit var addBookUseCase: AddBookUseCase

    @MockK
    protected lateinit var getBookByIdUseCase: GetBookByIdUseCase

    @MockK
    protected lateinit var updateBookDetailsUseCase: UpdateBookDetailsUseCase

    @MockK
    protected lateinit var updateBookCoverUseCase: UpdateBookCoverUseCase

    @MockK
    protected lateinit var deleteBookUseCase: DeleteBookUseCase

    @RelaxedMockK
    protected lateinit var multipartParser: MultipartParser

    @RelaxedMockK
    protected lateinit var bookValidator: BookValidator

    protected val imageBaseUrl = ""

    @BeforeAll
    fun setUpKoin() {
        MockKAnnotations.init(this)
        startKoin {
            val testModule = module {
                single {
                    BookController(
                        getBooksUseCase,
                        addBookUseCase,
                        getBookByIdUseCase,
                        updateBookDetailsUseCase,
                        updateBookCoverUseCase,
                        deleteBookUseCase,
                        bookValidator,
                        multipartParser,
                        BookMapperImpl(imageBaseUrl, GenreMapperImpl())
                    )
                }
                single { mockk<GenreController>() }
                single { mockk<TokenController>() }
                single { mockk<UserController>() }
                single { mockk<VerificationController>() }
            }
            modules(testModule)
        }
    }

    @AfterAll
    fun tearDownKoin() {
        stopKoin()
    }

    @BeforeEach
    fun resetMocks() {
        clearAllMocks()
    }
}