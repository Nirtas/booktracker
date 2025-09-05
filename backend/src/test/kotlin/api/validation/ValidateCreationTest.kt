package api.validation

import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import ru.jerael.booktracker.backend.api.dto.book.BookCreationDto
import ru.jerael.booktracker.backend.api.validation.BookValidator
import ru.jerael.booktracker.backend.domain.exceptions.ValidationException
import ru.jerael.booktracker.backend.domain.model.book.BookCreationPayload
import ru.jerael.booktracker.backend.domain.model.book.BookStatus
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ValidateCreationTest {

    private val validator: BookValidator = BookValidator()
    private val bookCreationDto = BookCreationDto(
        title = "Title",
        author = "Author",
        status = BookStatus.READ.value,
        genreIds = emptyList()
    )

    @Test
    fun `when dto is valid, validateCreation should return a correctly mapped BookCreationPayload`() = runTest {
        val expectedBookCreationPayload = BookCreationPayload(
            title = bookCreationDto.title,
            author = bookCreationDto.author,
            coverPath = null,
            status = BookStatus.fromString(bookCreationDto.status)!!,
            genreIds = bookCreationDto.genreIds
        )

        val result = validator.validateCreation(bookCreationDto)

        assertEquals(expectedBookCreationPayload, result)
    }

    @Test
    fun `when title is blank, a ValidationException should be thrown`() = runTest {
        val invalidDto = bookCreationDto.copy(title = "")

        assertFailsWith<ValidationException> {
            validator.validateCreation(invalidDto)
        }
    }

    @Test
    fun `when title is longer than 500 characters, a ValidationException should be thrown`() = runTest {
        val invalidDto = bookCreationDto.copy(title = "a".repeat(1000))

        assertFailsWith<ValidationException> {
            validator.validateCreation(invalidDto)
        }
    }

    @Test
    fun `when author is blank, a ValidationException should be thrown`() = runTest {
        val invalidDto = bookCreationDto.copy(author = "")

        assertFailsWith<ValidationException> {
            validator.validateCreation(invalidDto)
        }
    }

    @Test
    fun `when author is longer than 500 characters, a ValidationException should be thrown`() = runTest {
        val invalidDto = bookCreationDto.copy(author = "a".repeat(1000))

        assertFailsWith<ValidationException> {
            validator.validateCreation(invalidDto)
        }
    }

    @Test
    fun `when status is not valid, a ValidationException should be thrown`() = runTest {
        val invalidDto = bookCreationDto.copy(status = "invalid_status")

        assertFailsWith<ValidationException> {
            validator.validateCreation(invalidDto)
        }
    }
}