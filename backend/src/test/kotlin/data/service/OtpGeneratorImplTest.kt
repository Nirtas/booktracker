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

package data.service

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import ru.jerael.booktracker.backend.data.service.OtpGeneratorImpl
import ru.jerael.booktracker.backend.domain.service.OtpGenerator

class OtpGeneratorImplTest {

    private val otpCodeLength = 6
    private val otpGenerator: OtpGenerator = OtpGeneratorImpl(otpCodeLength)

    @Test
    fun `generate should return a string of correct length`() {
        val code = otpGenerator.generate()

        assertEquals(otpCodeLength, code.length)
    }

    @Test
    fun `generate should return a string containing only digits`() {
        val code = otpGenerator.generate()

        assertTrue(code.all { it.isDigit() })
    }
}