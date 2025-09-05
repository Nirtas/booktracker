package api.routes.genre

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import io.mockk.coEvery
import io.mockk.coVerify
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import ru.jerael.booktracker.backend.api.dto.ErrorDto
import ru.jerael.booktracker.backend.api.dto.genre.GenreDto
import ru.jerael.booktracker.backend.api.mappers.GenreMapperImpl
import ru.jerael.booktracker.backend.api.plugins.configureRouting
import ru.jerael.booktracker.backend.api.plugins.configureSerialization
import ru.jerael.booktracker.backend.api.plugins.configureStatusPages
import ru.jerael.booktracker.backend.domain.model.genre.Genre

class GetAllGenresRouteTest : GenresRouteTestBase() {

    private val url = "/api/genres"

    @Test
    fun `when genres exist, getAllGenres should return a list of genres and a 200 OK status`() = testApplication {
        val genres = listOf(
            Genre(1, "genre 1"),
            Genre(2, "genre 2"),
            Genre(3, "genre 3")
        )
        val genresDto = GenreMapperImpl().toDto(genres)
        coEvery { getGenresUseCase.invoke(any()) } returns genres

        application {
            configureStatusPages()
            configureSerialization()
            configureRouting()
        }
        val response = client.get(url)

        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(genresDto, Json.decodeFromString<List<GenreDto>>(response.bodyAsText()))
    }

    @Test
    fun `when genres not exist, getAllGenres should return an empty list and a 200 OK status`() = testApplication {
        coEvery { getGenresUseCase.invoke(any()) } returns emptyList()

        application {
            configureStatusPages()
            configureSerialization()
            configureRouting()
        }
        val response = client.get(url)

        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(emptyList<GenreDto>(), Json.decodeFromString<List<GenreDto>>(response.bodyAsText()))
    }

    @Test
    fun `when Accept-Language header is present, language() should correctly parse and return it`() = testApplication {
        coEvery { getGenresUseCase.invoke(any()) } returns emptyList()

        application {
            configureStatusPages()
            configureSerialization()
            configureRouting()
        }
        client.get(url) {
            header(HttpHeaders.AcceptLanguage, "en-US,en;q=0.9")
        }

        coVerify(exactly = 1) { getGenresUseCase.invoke("en") }
    }

    @Test
    fun `when getGenresUseCase is failed, an Exception should be thrown with 500 InternalServerError`() =
        testApplication {
            coEvery { getGenresUseCase.invoke(any()) } throws Exception("Error")

            application {
                configureStatusPages()
                configureSerialization()
                configureRouting()
            }
            val response = client.get(url)

            assertEquals(HttpStatusCode.InternalServerError, response.status)
            val errorDto = ErrorDto(
                code = "INTERNAL_SERVER_ERROR",
                message = "An unexpected error occurred. Please try again later."
            )
            assertEquals(errorDto, Json.decodeFromString<ErrorDto>(response.bodyAsText()))
        }
}