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

package ru.jerael.booktracker.backend.data.service

import org.apache.commons.mail.DefaultAuthenticator
import org.apache.commons.mail.SimpleEmail
import ru.jerael.booktracker.backend.domain.config.SmtpConfig
import ru.jerael.booktracker.backend.domain.exceptions.InternalException
import ru.jerael.booktracker.backend.domain.model.user.User
import ru.jerael.booktracker.backend.domain.repository.VerificationRepository
import ru.jerael.booktracker.backend.domain.service.OtpGenerator
import ru.jerael.booktracker.backend.domain.service.VerificationService
import java.time.LocalDateTime

class EmailVerificationService(
    private val verificationRepository: VerificationRepository,
    private val otpGenerator: OtpGenerator,
    private val smtpConfig: SmtpConfig,
    private val otpValidityMinutes: Long
) : VerificationService {

    override suspend fun start(user: User) {
        try {
            val code = otpGenerator.generate()
            val expiresAt = LocalDateTime.now().plusMinutes(otpValidityMinutes)
            verificationRepository.saveCode(user.id, code, expiresAt)
            sendEmail(user.email, code)
        } catch (e: Exception) {
            throw InternalException(message = "Error while sending an email")
        }
    }

    private fun sendEmail(address: String, code: String) {
        val simpleEmail = SimpleEmail().apply {
            hostName = smtpConfig.host
            setSmtpPort(smtpConfig.port)
            authenticator = DefaultAuthenticator(smtpConfig.user, smtpConfig.password)
            isSSLOnConnect = smtpConfig.ssl
            setFrom(smtpConfig.from)
            subject = "Your verification code for BookTracker"
            setMsg(createEmailBody(code))
            addTo(address)
        }
        simpleEmail.send()
    }

    private fun createEmailBody(code: String): String {
        return """
            Hello!

            Thank you for registering at BookTracker.
            Your verification code is: $code

            This code will expire in $otpValidityMinutes minutes.

            If you did not request this, please ignore this email.
        """.trimIndent()
    }
}