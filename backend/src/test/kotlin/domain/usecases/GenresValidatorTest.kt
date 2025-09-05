package domain.usecases

import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import ru.jerael.booktracker.backend.domain.exceptions.ValidationException
import ru.jerael.booktracker.backend.domain.model.genre.Genre
import ru.jerael.booktracker.backend.domain.repository.GenreRepository
import ru.jerael.booktracker.backend.domain.usecases.GenresValidator

class GenresValidatorTest {

    @MockK
    private lateinit var genreRepository: GenreRepository

    private lateinit var validator: GenresValidator

    private val language = "en"
    private val foundGenres = listOf(
        Genre(1, "genre 1"),
        Genre(2, "genre 2"),
        Genre(3, "genre 3")
    )

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        validator = GenresValidator(genreRepository)
    }

    @Test
    fun `when genreIds list is empty, the validation should complete without errors and not call repository`() =
        runTest {
            val genreIds: List<Int> = emptyList()

            assertDoesNotThrow {
                validator.invoke(genreIds, language)
            }

            coVerify(exactly = 0) { genreRepository.getGenresByIds(any(), any()) }
        }

    @Test
    fun `when all genres exist, the validation should complete without errors`() = runTest {
        val genreIds: List<Int> = listOf(1, 2, 3, 2)
        val distinctIds: List<Int> = genreIds.distinct()
        coEvery { genreRepository.getGenresByIds(distinctIds, language) } returns foundGenres

        assertDoesNotThrow {
            validator.invoke(genreIds, language)
        }

        coVerify(exactly = 1) { genreRepository.getGenresByIds(distinctIds, language) }
    }

    @Test
    fun `when one or more genres are not found, a ValidationException should be thrown`() = runTest {
        val genreIds: List<Int> = listOf(1, 2, 3, 2, 4)
        val distinctIds: List<Int> = genreIds.distinct()
        coEvery { genreRepository.getGenresByIds(distinctIds, language) } returns foundGenres

        val exception = assertThrows<ValidationException> {
            validator.invoke(genreIds, language)
        }

        assertTrue(exception.message!!.contains("4"))
        coVerify(exactly = 1) { genreRepository.getGenresByIds(distinctIds, language) }
    }
}