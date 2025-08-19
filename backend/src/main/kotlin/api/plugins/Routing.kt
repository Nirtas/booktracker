package ru.jerael.booktracker.backend.api.plugins

import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import ru.jerael.booktracker.backend.api.controller.BookController
import ru.jerael.booktracker.backend.api.controller.GenreController
import ru.jerael.booktracker.backend.api.routes.books
import ru.jerael.booktracker.backend.api.routes.genres

fun Application.configureRouting() {
    val bookController: BookController by inject()
    val genreController: GenreController by inject()

    routing {
        route("/api") {
            books(bookController)
            genres(genreController)
        }
    }
}