package ru.jerael.booktracker.backend.api.routes

import io.ktor.server.response.*
import io.ktor.server.routing.*
import ru.jerael.booktracker.backend.data.dto.BookDto
import ru.jerael.booktracker.backend.data.mappers.toBookDto
import ru.jerael.booktracker.backend.domain.usecases.GetBooksUseCase

fun Route.bookRoutes(getBooksUseCase: GetBooksUseCase, imageBaseUrl: String) {
    route("/books") {
        get {
            val books = getBooksUseCase()
            val bookDtos: List<BookDto> = books.map { it.toBookDto(imageBaseUrl) }
            call.respond(bookDtos)
        }
    }
}