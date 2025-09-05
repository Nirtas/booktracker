package api.validation

import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import ru.jerael.booktracker.backend.api.validation.BookValidator
import ru.jerael.booktracker.backend.domain.exceptions.ValidationException
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