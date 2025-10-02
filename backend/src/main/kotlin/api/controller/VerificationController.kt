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

package ru.jerael.booktracker.backend.api.controller

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import ru.jerael.booktracker.backend.api.dto.verification.VerificationDto
import ru.jerael.booktracker.backend.api.dto.verification.VerificationResendCodeDto
import ru.jerael.booktracker.backend.api.mappers.TokenMapper
import ru.jerael.booktracker.backend.domain.model.verification.VerificationPayload
import ru.jerael.booktracker.backend.domain.usecases.verification.ResendVerificationCodeUseCase
import ru.jerael.booktracker.backend.domain.usecases.verification.VerifyCodeUseCase

class VerificationController(
    private val verifyCodeUseCase: VerifyCodeUseCase,
    private val resendVerificationCodeUseCase: ResendVerificationCodeUseCase,
    private val tokenMapper: TokenMapper
) {
    suspend fun verify(call: ApplicationCall) {
        val verificationDto = call.receive<VerificationDto>()
        val verificationPayload = VerificationPayload(
            email = verificationDto.email,
            code = verificationDto.code
        )
        val newTokenPair = verifyCodeUseCase(verificationPayload)
        call.respond(HttpStatusCode.OK, tokenMapper.mapTokenToResponseDto(newTokenPair))
    }

    suspend fun resendCode(call: ApplicationCall) {
        val verificationResendCodeDto = call.receive<VerificationResendCodeDto>()
        resendVerificationCodeUseCase(verificationResendCodeDto.email)
        call.respond(HttpStatusCode.OK)
    }
}