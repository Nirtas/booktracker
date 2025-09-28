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

package api.routes.genre

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.mockk.MockKAnnotations
import io.mockk.clearAllMocks
import io.mockk.impl.annotations.MockK
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
import ru.jerael.booktracker.backend.api.dto.ErrorDto
import ru.jerael.booktracker.backend.api.mappers.GenreMapperImpl
import ru.jerael.booktracker.backend.domain.usecases.genre.GetGenresUseCase
import java.util.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class GenresRouteTestBase : KoinTest {

    @MockK
    protected lateinit var getGenresUseCase: GetGenresUseCase

    protected val secret = "secret"
    protected val issuer = "issuer"
    protected val audience = "audience"
    protected val testRealm = "realm"

    protected fun generateTestToken(userId: UUID): String {
        return JWT.create()
            .withAudience(audience)
            .withIssuer(issuer)
            .withClaim("userId", userId.toString())
            .withExpiresAt(Date(System.currentTimeMillis() + 15L * 60 * 1000))
            .sign(Algorithm.HMAC256(secret))
    }

    protected fun Application.configureTestAuthentication() {
        install(Authentication) {
            jwt("auth-jwt") {
                this.realm = testRealm
                verifier(
                    JWT
                        .require(Algorithm.HMAC256(secret))
                        .withAudience(audience)
                        .withIssuer(issuer)
                        .build()
                )
                validate { credential ->
                    val userId = credential.payload.getClaim("userId").asString()
                    if (userId != null && userId.isNotBlank()) {
                        UserIdPrincipal(userId)
                    } else {
                        null
                    }
                }
                challenge { _, _ ->
                    val errorDto = ErrorDto(
                        code = "INVALID_TOKEN",
                        message = "Token is not valid or has expired."
                    )
                    call.respond(HttpStatusCode.Unauthorized, errorDto)
                }
            }
        }
    }

    @BeforeAll
    fun setUpKoin() {
        MockKAnnotations.init(this)
        startKoin {
            val testModule = module {
                single { GenreController(getGenresUseCase, GenreMapperImpl()) }
                single { mockk<BookController>() }
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