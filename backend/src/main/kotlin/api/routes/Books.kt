package ru.jerael.booktracker.backend.api.routes

import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import ru.jerael.booktracker.backend.data.dto.BookCreationDto
import ru.jerael.booktracker.backend.data.dto.BookDto
import ru.jerael.booktracker.backend.data.dto.BookUpdateDto
import ru.jerael.booktracker.backend.data.mappers.toBookDto
import ru.jerael.booktracker.backend.domain.model.BookCoverUpdatePayload
import ru.jerael.booktracker.backend.domain.model.BookCreationPayload
import ru.jerael.booktracker.backend.domain.model.BookDetailsUpdatePayload
import ru.jerael.booktracker.backend.domain.storage.FileStorage
import ru.jerael.booktracker.backend.domain.usecases.*
import java.io.File
import java.util.*

private const val COVERS_PATH_PREFIX = "covers"

fun Route.books(
    getBooksUseCase: GetBooksUseCase,
    addBookUseCase: AddBookUseCase,
    getBookByIdUseCase: GetBookByIdUseCase,
    updateBookDetailsUseCase: UpdateBookDetailsUseCase,
    updateBookCoverUseCase: UpdateBookCoverUseCase,
    deleteBookUseCase: DeleteBookUseCase,
    fileStorage: FileStorage,
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
                            coverPath = "$COVERS_PATH_PREFIX/${UUID.randomUUID()}.$fileExtension"
                            val channel = part.provider()
                            fileStorage.saveFile(coverPath!!, channel)
                        }
                    }

                    else -> {}
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
        route("/{id}") {
            get {
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
            delete {
                val idParameter = call.parameters["id"]
                if (idParameter == null) {
                    call.respond(HttpStatusCode.BadRequest, "Book ID is missing")
                    return@delete
                }
                val id: UUID
                try {
                    id = UUID.fromString(idParameter)
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, "Error")
                    return@delete
                }
                val wasDeleted = deleteBookUseCase(id)
                if (!wasDeleted) {
                    call.respond(HttpStatusCode.NotFound, "Book not found")
                } else {
                    call.respond(HttpStatusCode.NoContent)
                }
            }
            put {
                val idParameter = call.parameters["id"]
                if (idParameter == null) {
                    call.respond(HttpStatusCode.BadRequest, "Book ID is missing")
                    return@put
                }
                val id: UUID
                try {
                    id = UUID.fromString(idParameter)
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, "Error")
                    return@put
                }
                val bookUpdateDto = call.receive<BookUpdateDto>()
                val bookDetailsUpdatePayload = BookDetailsUpdatePayload(
                    title = bookUpdateDto.title,
                    author = bookUpdateDto.author
                )
                val book = updateBookDetailsUseCase(id, bookDetailsUpdatePayload)
                if (book == null) {
                    call.respond(HttpStatusCode.NotFound, "Book not found")
                } else {
                    val bookDto = book.toBookDto(imageBaseUrl)
                    call.respond(bookDto)
                }
            }
            post("/cover") {
                val idParameter = call.parameters["id"]
                if (idParameter == null) {
                    call.respond(HttpStatusCode.BadRequest, "Book ID is missing")
                    return@post
                }
                val id: UUID
                try {
                    id = UUID.fromString(idParameter)
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, "Error")
                    return@post
                }
                val part = call.receiveMultipart().readPart()
                if (part !is PartData.FileItem || part.name != "cover" || part.originalFileName.isNullOrBlank()) {
                    part?.dispose?.let { it() }
                    call.respond(HttpStatusCode.BadRequest, "File part 'cover' is missing or invalid")
                    return@post
                }
                val existingBook = getBookByIdUseCase(id)
                if (existingBook == null) {
                    part.dispose()
                    call.respond(HttpStatusCode.NotFound, "Book not found")
                    return@post
                }
                existingBook.coverPath?.let {
                    fileStorage.deleteFile(it)
                }
                val fileExtension = File(part.originalFileName as String).extension
                val coverPath = "$COVERS_PATH_PREFIX/${UUID.randomUUID()}.$fileExtension"
                val channel = part.provider()
                fileStorage.saveFile(coverPath, channel)
                part.dispose()
                val bookCoverUpdatePayload = BookCoverUpdatePayload(
                    coverPath = coverPath
                )
                val book = updateBookCoverUseCase(id, bookCoverUpdatePayload)
                if (book == null) {
                    call.respond(HttpStatusCode.NotFound, "Book not found")
                } else {
                    val bookDto = book.toBookDto(imageBaseUrl)
                    call.respond(bookDto)
                }
            }
        }
    }
}