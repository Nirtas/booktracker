package ru.jerael.booktracker.backend.api.controller

import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import kotlinx.serialization.json.Json
import ru.jerael.booktracker.backend.data.dto.book.BookCreationDto
import ru.jerael.booktracker.backend.data.dto.book.BookDto
import ru.jerael.booktracker.backend.data.dto.book.BookUpdateDto
import ru.jerael.booktracker.backend.data.mappers.toBookDto
import ru.jerael.booktracker.backend.domain.model.book.BookCreationPayload
import ru.jerael.booktracker.backend.domain.model.book.BookDetailsUpdatePayload
import ru.jerael.booktracker.backend.domain.storage.CoverStorage
import ru.jerael.booktracker.backend.domain.usecases.book.*
import java.util.*

class BookController(
    private val getBooksUseCase: GetBooksUseCase,
    private val addBookUseCase: AddBookUseCase,
    private val getBookByIdUseCase: GetBookByIdUseCase,
    private val updateBookDetailsUseCase: UpdateBookDetailsUseCase,
    private val updateBookCoverUseCase: UpdateBookCoverUseCase,
    private val deleteBookUseCase: DeleteBookUseCase,
    private val coverStorage: CoverStorage,
    private val imageBaseUrl: String
) {

    suspend fun getAllBooks(call: ApplicationCall) {
        val books = getBooksUseCase()
        val bookDtos: List<BookDto> = books.map { it.toBookDto(imageBaseUrl) }
        call.respond(bookDtos)
    }

    suspend fun addBook(call: ApplicationCall) {
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
                        coverPath = coverStorage.save(part)
                    }
                }

                else -> {}
            }
            part.dispose()
        }
        if (bookCreationDto == null) {
            call.respond(HttpStatusCode.BadRequest, "Form item 'book' is missing")
            return
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

    suspend fun getBookById(call: ApplicationCall) {
        val id = call.getIdOrRespondError() ?: return
        getBookByIdUseCase(id)?.let { book ->
            val bookDto = book.toBookDto(imageBaseUrl)
            call.respond(bookDto)
        } ?: call.respond(HttpStatusCode.NotFound, "Book not found")
    }

    suspend fun deleteBook(call: ApplicationCall) {
        val id = call.getIdOrRespondError() ?: return
        if (deleteBookUseCase(id)) {
            call.respond(HttpStatusCode.NoContent)
        } else {
            call.respond(HttpStatusCode.NotFound, "Book not found")
        }
    }

    suspend fun updateBookDetails(call: ApplicationCall) {
        val id = call.getIdOrRespondError() ?: return
        val bookUpdateDto = call.receive<BookUpdateDto>()
        val bookDetailsUpdatePayload = BookDetailsUpdatePayload(
            title = bookUpdateDto.title,
            author = bookUpdateDto.author
        )
        updateBookDetailsUseCase(id, bookDetailsUpdatePayload)?.let { book ->
            val bookDto = book.toBookDto(imageBaseUrl)
            call.respond(bookDto)
        } ?: call.respond(HttpStatusCode.NotFound, "Book not found")
    }

    suspend fun updateBookCover(call: ApplicationCall) {
        val id = call.getIdOrRespondError() ?: return
        val part = call.receiveMultipart().readPart()
        try {
            if (part !is PartData.FileItem || part.name != "cover" || part.originalFileName.isNullOrBlank()) {
                call.respond(HttpStatusCode.BadRequest, "File part 'cover' is missing or invalid")
                return
            }
            updateBookCoverUseCase(id, part)?.let { book ->
                val bookDto = book.toBookDto(imageBaseUrl)
                call.respond(bookDto)
            } ?: call.respond(HttpStatusCode.NotFound, "Book not found")
        } finally {
            part?.dispose?.let { it() }
        }
    }

    private suspend fun ApplicationCall.getIdOrRespondError(): UUID? {
        val idParameter = parameters["id"]
        if (idParameter == null) {
            respond(HttpStatusCode.BadRequest, "Book ID is missing")
            return null
        }
        return try {
            UUID.fromString(idParameter)
        } catch (e: Exception) {
            respond(HttpStatusCode.BadRequest, "Invalid Book ID format")
            null
        }
    }
}