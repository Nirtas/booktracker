package ru.jerael.booktracker.backend.api.routes

import io.ktor.server.routing.*
import ru.jerael.booktracker.backend.api.controller.GenreController

fun Route.genres(
    genreController: GenreController
) {
    route("/genres") {
        get {
            genreController.getAllGenres(call)
        }
    }
}