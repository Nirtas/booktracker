package ru.jerael.booktracker.backend.api.controller

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import ru.jerael.booktracker.backend.api.util.language
import ru.jerael.booktracker.backend.data.dto.genre.GenreDto
import ru.jerael.booktracker.backend.data.mappers.toGenreDto
import ru.jerael.booktracker.backend.domain.usecases.genre.GetGenresUseCase

class GenreController(
    private val getGenresUseCase: GetGenresUseCase
) {

    suspend fun getAllGenres(call: ApplicationCall) {
        val language = call.request.language()
        val genres = getGenresUseCase(language)
        val genreDtos: List<GenreDto> = genres.map { it.toGenreDto() }
        call.respond(HttpStatusCode.OK, genreDtos)
    }
}