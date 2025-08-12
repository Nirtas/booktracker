package ru.jerael.booktracker.backend.api.routes

import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.cio.*
import io.ktor.utils.io.*
import kotlinx.serialization.json.Json
import ru.jerael.booktracker.backend.data.dto.BookCreationDto
import ru.jerael.booktracker.backend.data.dto.BookDto
import ru.jerael.booktracker.backend.data.mappers.toBookDto
import ru.jerael.booktracker.backend.domain.model.BookCreationPayload
import ru.jerael.booktracker.backend.domain.usecases.AddBookUseCase
import ru.jerael.booktracker.backend.domain.usecases.GetBookByIdUseCase
import ru.jerael.booktracker.backend.domain.usecases.GetBooksUseCase
import java.io.File
import java.util.*

private const val COVERS_PATH_PREFIX = "covers"

fun Route.books(
    getBooksUseCase: GetBooksUseCase,
    addBookUseCase: AddBookUseCase,
    getBookByIdUseCase: GetBookByIdUseCase,
    imageBaseUrl: String
) {
    route("/books") {
        get {
            val books = getBooksUseCase()
            val bookDtos: List<BookDto> = books.map { it.toBookDto(imageBaseUrl) }
            call.respond(bookDtos)
        }
        post {
            var bookCreationDto: BookCreationDto? = null
            var coverPath: String? = null
            val persistentStoragePath =
                call.application.environment.config.property("ktor.storage.persistentPath").getString()
            val coversDir = File(persistentStoragePath, COVERS_PATH_PREFIX)
            coversDir.mkdirs()
            val multipartData = call.receiveMultipart()
            multipartData.forEachPart { part ->
                when (part) {
                    is PartData.FormItem -> {
                        if (part.name == "book") {
                            bookCreationDto = Json.decodeFromString(part.value)
                        }
                    }

                    is PartData.FileItem -> {
                        if (part.name == "cover" && part.originalFileName?.isNotBlank() == true) {
                            val fileExtension = File(part.originalFileName as String).extension
                            val coverFilename = "${UUID.randomUUID()}.$fileExtension"
                            coverPath = "$COVERS_PATH_PREFIX/$coverFilename"
                            val file = File(coversDir, coverFilename)
                            part.provider().copyAndClose(file.writeChannel())
                        }
                    }

                    else -> {
                        part.dispose()
                    }
                }
                part.dispose()
            }
            val bookCreationPayload = BookCreationPayload(
                title = bookCreationDto!!.title,
                author = bookCreationDto!!.author,
                coverPath = coverPath
            )
            val newBook = addBookUseCase(bookCreationPayload)
            val newBookDto = newBook.toBookDto(imageBaseUrl)
            call.respond(newBookDto)
        }
        get("/{id}") {
            val idParameter = call.parameters["id"]
            if (idParameter == null) {
                call.respond(HttpStatusCode.BadRequest, "Book ID is missing")
                return@get
            }
            val id: UUID
            try {
                id = UUID.fromString(idParameter)
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, "Error")
                return@get
            }
            val book = getBookByIdUseCase(id)
            if (book == null) {
                call.respond(HttpStatusCode.NotFound, "Book not found")
            } else {
                val bookDto = book.toBookDto(imageBaseUrl)
                call.respond(bookDto)
            }
        }
    }
}