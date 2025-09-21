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

package api.validation

import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import ru.jerael.booktracker.backend.api.validation.ValidationException
import ru.jerael.booktracker.backend.api.validation.validator.BookValidator
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ValidateIdTest {

    private val validator: BookValidator = BookValidator()

    @Test
    fun `when id is a valid UUID string, the correct UUID should be returned`() = runTest {
        val bookId = UUID.randomUUID()

        val result = validator.validateId(bookId.toString())

        assertEquals(bookId, result)
    }

    @Test
    fun `when id is null, a ValidationException should be thrown`() = runTest {
        assertFailsWith<ValidationException> {
            validator.validateId(null)
        }
    }

    @Test
    fun `when id is not a valid UUID string, a ValidationException should be thrown`() = runTest {
        assertFailsWith<ValidationException> {
            validator.validateId("invalid uuid string")
        }
    }
}