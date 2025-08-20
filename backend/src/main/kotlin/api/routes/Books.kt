package ru.jerael.booktracker.backend.api.routes

import io.ktor.server.routing.*
import ru.jerael.booktracker.backend.api.controller.BookController

fun Route.books(
    bookController: BookController
) {
    route("/books") {
        get {
            bookController.getAllBooks(call)
        }
        post {
            bookController.addBook(call)
        }
        route("/{id}") {
            get {
                bookController.getBookById(call)
            }
            delete {
                bookController.deleteBook(call)
            }
            put {
                bookController.updateBookDetails(call)
            }
            post("/cover") {
                bookController.updateBookCover(call)
            }
        }
    }
}