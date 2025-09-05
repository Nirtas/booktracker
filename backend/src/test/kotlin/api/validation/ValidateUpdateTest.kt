package api.validation

import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import ru.jerael.booktracker.backend.api.dto.book.BookUpdateDto
import ru.jerael.booktracker.backend.api.validation.BookValidator
import ru.jerael.booktracker.backend.domain.exceptions.ValidationException
import ru.jerael.booktracker.backend.domain.model.book.BookDetailsUpdatePayload
import ru.jerael.booktracker.backend.domain.model.book.BookStatus
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ValidateUpdateTest {

    private val validator: BookValidator = BookValidator()
    private val bookUpdateDto = BookUpdateDto(
        title = "Title",
        author = "Author",
        status = BookStatus.READ.value,
        genreIds = emptyList()
    )

    @Test
    fun `when dto is valid, validateCreation should return a correctly mapped BookCreationPayload`() = runTest {
        val expectedBookUpdatePayload = BookDetailsUpdatePayload(
            title = bookUpdateDto.title,
            author = bookUpdateDto.author,
            status = BookStatus.fromString(bookUpdateDto.status)!!,
            genreIds = bookUpdateDto.genreIds
        )

        val result = validator.validateUpdate(bookUpdateDto)

        assertEquals(expectedBookUpdatePayload, result)
    }

    @Test
    fun `when title is blank, a ValidationException should be thrown`() = runTest {
        val invalidDto = bookUpdateDto.copy(title = "")

        assertFailsWith<ValidationException> {
            validator.validateUpdate(invalidDto)
        }
    }

    @Test
    fun `when title is longer than 500 characters, a ValidationException should be thrown`() = runTest {
        val invalidDto = bookUpdateDto.copy(title = "a".repeat(1000))

        assertFailsWith<ValidationException> {
            validator.validateUpdate(invalidDto)
        }
    }

    @Test
    fun `when author is blank, a ValidationException should be thrown`() = runTest {
        val invalidDto = bookUpdateDto.copy(author = "")

        assertFailsWith<ValidationException> {
            validator.validateUpdate(invalidDto)
        }
    }

    @Test
    fun `when author is longer than 500 characters, a ValidationException should be thrown`() = runTest {
        val invalidDto = bookUpdateDto.copy(author = "a".repeat(1000))

        assertFailsWith<ValidationException> {
            validator.validateUpdate(invalidDto)
        }
    }

    @Test
    fun `when status is not valid, a ValidationException should be thrown`() = runTest {
        val invalidDto = bookUpdateDto.copy(status = "invalid_status")

        assertFailsWith<ValidationException> {
            validator.validateUpdate(invalidDto)
        }
    }
}