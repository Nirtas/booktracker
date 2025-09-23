package api.validation.validator

import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import ru.jerael.booktracker.backend.api.dto.book.BookCreationDto
import ru.jerael.booktracker.backend.api.dto.book.BookUpdateDto
import ru.jerael.booktracker.backend.api.validation.ValidationException
import ru.jerael.booktracker.backend.api.validation.validator.BookValidator
import ru.jerael.booktracker.backend.domain.model.book.BookStatus
import kotlin.test.assertTrue

class BookValidatorTest {

    private val validator: BookValidator = BookValidator()
    private val bookCreationDto = BookCreationDto(
        title = "Title",
        author = "Author",
        status = BookStatus.READ.value,
        genreIds = emptyList()
    )
    private val bookUpdateDto = BookUpdateDto(
        title = "Title",
        author = "Author",
        status = BookStatus.READ.value,
        genreIds = emptyList()
    )

    @Test
    fun `when dto is valid, validateCreation shouldn't throw exception`() = runTest {
        assertDoesNotThrow {
            validator.validateCreation(bookCreationDto)
        }
    }

    @Test
    fun `when title is invalid, validateCreation should throw ValidationException`() {
        val invalidDto = bookCreationDto.copy(title = "")

        val exception = assertThrows<ValidationException> {
            validator.validateCreation(invalidDto)
        }

        assertTrue(exception.errors.containsKey("title"))
    }

    @Test
    fun `when multiple fields are invalid, validateCreation should throw ValidationException containing all errors`() {
        val invalidDto = bookCreationDto.copy(title = "", author = "")

        val exception = assertThrows<ValidationException> {
            validator.validateCreation(invalidDto)
        }

        assertTrue(exception.errors.containsKey("title"))
        assertTrue(exception.errors.containsKey("author"))
    }

    @Test
    fun `when dto is valid, validateUpdate shouldn't throw exception`() = runTest {
        assertDoesNotThrow {
            validator.validateUpdate(bookUpdateDto)
        }
    }

    @Test
    fun `when title is invalid, validateUpdate should throw ValidationException`() {
        val invalidDto = bookUpdateDto.copy(title = "")

        val exception = assertThrows<ValidationException> {
            validator.validateUpdate(invalidDto)
        }

        assertTrue(exception.errors.containsKey("title"))
    }

    @Test
    fun `when multiple fields are invalid, validateUpdate should throw ValidationException containing all errors`() {
        val invalidDto = bookUpdateDto.copy(title = "", author = "")

        val exception = assertThrows<ValidationException> {
            validator.validateUpdate(invalidDto)
        }

        assertTrue(exception.errors.containsKey("title"))
        assertTrue(exception.errors.containsKey("author"))
    }
}