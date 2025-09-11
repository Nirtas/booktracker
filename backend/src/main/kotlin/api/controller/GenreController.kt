package ru.jerael.booktracker.backend.api.controller

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import ru.jerael.booktracker.backend.api.mappers.GenreMapper
import ru.jerael.booktracker.backend.api.util.language
import ru.jerael.booktracker.backend.domain.usecases.genre.GetGenresUseCase

class GenreController(
    private val getGenresUseCase: GetGenresUseCase,
    private val genreMapper: GenreMapper
) {

    suspend fun getAllGenres(call: ApplicationCall) {
        val language = call.request.language()
        val genres = getGenresUseCase(language)
        call.respond(HttpStatusCode.OK, genreMapper.mapGenresToDtos(genres))
    }
}