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

package api.routes.verification

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
import ru.jerael.booktracker.backend.api.mappers.VerificationMapper
import ru.jerael.booktracker.backend.api.validation.validator.VerificationValidator
import ru.jerael.booktracker.backend.domain.usecases.verification.ResendVerificationCodeUseCase
import ru.jerael.booktracker.backend.domain.usecases.verification.VerifyCodeUseCase

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class VerificationsRouteTestBase : KoinTest {

    @MockK
    protected lateinit var verifyCodeUseCase: VerifyCodeUseCase

    @MockK
    protected lateinit var resendVerificationCodeUseCase: ResendVerificationCodeUseCase

    @RelaxedMockK
    protected lateinit var verificationValidator: VerificationValidator

    @RelaxedMockK
    protected lateinit var verificationMapper: VerificationMapper

    @BeforeAll
    fun setUpKoin() {
        MockKAnnotations.init(this)
        startKoin {
            val testModule = module {
                single { mockk<TokenController>() }
                single { mockk<BookController>() }
                single { mockk<GenreController>() }
                single { mockk<UserController>() }
                single {
                    VerificationController(
                        verifyCodeUseCase,
                        resendVerificationCodeUseCase,
                        verificationValidator,
                        verificationMapper
                    )
                }
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