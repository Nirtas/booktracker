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

package api.routes.token

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import io.mockk.*
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.*
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import ru.jerael.booktracker.backend.api.controller.*
import ru.jerael.booktracker.backend.api.dto.ErrorDto
import ru.jerael.booktracker.backend.api.dto.login.LoginRequestDto
import ru.jerael.booktracker.backend.api.dto.login.LoginResponseDto
import ru.jerael.booktracker.backend.api.mappers.LoginMapper
import ru.jerael.booktracker.backend.api.mappers.TokenMapper
import ru.jerael.booktracker.backend.api.plugins.configureRouting
import ru.jerael.booktracker.backend.api.plugins.configureSerialization
import ru.jerael.booktracker.backend.api.plugins.configureStatusPages
import ru.jerael.booktracker.backend.api.validation.validator.LoginValidator
import ru.jerael.booktracker.backend.domain.model.token.Token
import ru.jerael.booktracker.backend.domain.usecases.login.LoginUseCase
import kotlin.test.assertEquals

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LoginRouteTest : KoinTest {

    @MockK
    private lateinit var loginUseCase: LoginUseCase

    @RelaxedMockK
    private lateinit var loginValidator: LoginValidator

    @RelaxedMockK
    private lateinit var loginMapper: LoginMapper

    @RelaxedMockK
    private lateinit var tokenMapper: TokenMapper

    private val url = "/api/tokens"
    private val json = Json.encodeToString(LoginRequestDto("test@example.com", "Passw0rd!"))
    private val token = Token("token", 10L)
    private val loginResponseDto = LoginResponseDto(token.token, token.expiresIn)
    private val errorDto = ErrorDto(
        code = "INTERNAL_SERVER_ERROR",
        message = "An unexpected error occurred. Please try again later."
    )

    @BeforeAll
    fun setUpKoin() {
        MockKAnnotations.init(this)
        startKoin {
            val testModule = module {
                single {
                    TokenController(
                        loginUseCase,
                        loginValidator,
                        loginMapper,
                        tokenMapper
                    )
                }
                single { mockk<BookController>() }
                single { mockk<GenreController>() }
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

    @Test
    fun `when request is valid and credentials are valid for a verified user, login should return a token and a 200 OK status`() =
        testApplication {
            coEvery { loginUseCase.invoke(any()) } returns token
            every { tokenMapper.mapTokenToResponseDto(token) } returns loginResponseDto

            application {
                configureStatusPages()
                configureSerialization()
                configureRouting()
            }
            val response = client.post(url) {
                contentType(ContentType.Application.Json)
                setBody(json)
            }

            assertEquals(HttpStatusCode.OK, response.status)
            assertEquals(loginResponseDto, Json.decodeFromString<LoginResponseDto>(response.bodyAsText()))
            verify(exactly = 1) { loginValidator.validateLogin(any()) }
            verify(exactly = 1) { loginMapper.mapDtoToPayload(any()) }
            coVerify(exactly = 1) { loginUseCase.invoke(any()) }
            verify(exactly = 1) { tokenMapper.mapTokenToResponseDto(any()) }
        }

    @Test
    fun `when validateLogin is failed, an Exception should be thrown with 500 InternalServerError`() = testApplication {
        every { loginValidator.validateLogin(any()) } throws Exception("Error")

        application {
            configureStatusPages()
            configureSerialization()
            configureRouting()
        }
        val response = client.post(url) {
            contentType(ContentType.Application.Json)
            setBody(json)
        }

        assertEquals(HttpStatusCode.InternalServerError, response.status)
        assertEquals(errorDto, Json.decodeFromString<ErrorDto>(response.bodyAsText()))
        coVerify(exactly = 0) { loginUseCase.invoke(any()) }
    }

    @Test
    fun `when loginUseCase is failed, an Exception should be thrown with 500 InternalServerError`() = testApplication {
        coEvery { loginUseCase.invoke(any()) } throws Exception("Error")

        application {
            configureStatusPages()
            configureSerialization()
            configureRouting()
        }
        val response = client.post(url) {
            contentType(ContentType.Application.Json)
            setBody(json)
        }

        assertEquals(HttpStatusCode.InternalServerError, response.status)
        assertEquals(errorDto, Json.decodeFromString<ErrorDto>(response.bodyAsText()))
    }
}