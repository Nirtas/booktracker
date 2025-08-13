package ru.jerael.booktracker.backend.api.plugins

import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import ru.jerael.booktracker.backend.api.routes.books
import ru.jerael.booktracker.backend.domain.storage.FileStorage
import ru.jerael.booktracker.backend.domain.usecases.*

fun Application.configureRouting() {
    val getBooksUseCase: GetBooksUseCase by inject()
    val addBookUseCase: AddBookUseCase by inject()
    val getBookByIdUseCase: GetBookByIdUseCase by inject()
    val updateBookDetailsUseCase: UpdateBookDetailsUseCase by inject()
    val updateBookCoverUseCase: UpdateBookCoverUseCase by inject()
    val deleteBookUseCase: DeleteBookUseCase by inject()
    val fileStorage: FileStorage by inject()
    val imageBaseUrl: String = environment.config.property("ktor.storage.baseUrl").getString()

    routing {
        route("/api") {
            books(
                getBooksUseCase = getBooksUseCase,
                addBookUseCase = addBookUseCase,
                getBookByIdUseCase = getBookByIdUseCase,
                updateBookDetailsUseCase = updateBookDetailsUseCase,
                updateBookCoverUseCase = updateBookCoverUseCase,
                deleteBookUseCase = deleteBookUseCase,
                fileStorage = fileStorage,
                imageBaseUrl = imageBaseUrl
            )
        }
    }
}