package ru.jerael.booktracker.backend.api.plugins

import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import ru.jerael.booktracker.backend.api.routes.books
import ru.jerael.booktracker.backend.domain.usecases.AddBookUseCase
import ru.jerael.booktracker.backend.domain.usecases.GetBooksUseCase

fun Application.configureRouting() {
    val getBooksUseCase: GetBooksUseCase by inject()
    val addBookUseCase: AddBookUseCase by inject()
    val imageBaseUrl: String = environment.config.property("ktor.storage.baseUrl").getString()

    routing {
        route("/api") {
            books(
                getBooksUseCase = getBooksUseCase,
                addBookUseCase = addBookUseCase,
                imageBaseUrl = imageBaseUrl
            )
        }
    }
}