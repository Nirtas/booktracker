package ru.jerael.booktracker.backend.api.plugins

import io.github.cdimascio.dotenv.dotenv
import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import ru.jerael.booktracker.backend.api.routes.books
import ru.jerael.booktracker.backend.domain.usecases.AddBookUseCase
import ru.jerael.booktracker.backend.domain.usecases.GetBooksUseCase

fun Application.configureRouting() {
    val getBooksUseCase: GetBooksUseCase by inject()
    val addBookUseCase: AddBookUseCase by inject()
    val imageBaseUrl: String = dotenv()["STORAGE_BASE_URL"]
    val coversPathPrefix: String = dotenv()["COVERS_PATH_PREFIX"]

    routing {
        route("/api") {
            books(
                getBooksUseCase = getBooksUseCase,
                addBookUseCase = addBookUseCase,
                imageBaseUrl = imageBaseUrl,
                coversPathPrefix = coversPathPrefix
            )
        }
    }
}