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
import ru.jerael.booktracker.backend.domain.exceptions.ValidationException
import ru.jerael.booktracker.backend.domain.model.book.BookCreationPayload
import ru.jerael.booktracker.backend.domain.model.book.BookDetailsUpdatePayload
import ru.jerael.booktracker.backend.domain.model.book.BookStatus
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
        call.respond(HttpStatusCode.OK, bookDtos)
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
        val dto = bookCreationDto ?: throw ValidationException("Form item 'book' is missing or has invalid format.")
        if (dto.title.isBlank()) throw ValidationException("Book title can't be empty.")
        if (dto.author.isBlank()) throw ValidationException("Book author can't be empty.")
        val status = BookStatus.fromString(dto.status)
            ?: throw ValidationException("Invalid status: ${dto.status}. Allowed values are: ${BookStatus.entries.joinToString()}")
        val bookCreationPayload = BookCreationPayload(
            title = dto.title,
            author = dto.author,
            coverPath = coverPath,
            status = status,
            genreIds = dto.genreIds
        )
        val newBook = addBookUseCase(bookCreationPayload)
        val newBookDto = newBook.toBookDto(imageBaseUrl)
        call.respond(HttpStatusCode.Created, newBookDto)
    }

    suspend fun getBookById(call: ApplicationCall) {
        val id = call.getIdOrRespondError()
        val book = getBookByIdUseCase(id)
        val bookDto = book.toBookDto(imageBaseUrl)
        call.respond(HttpStatusCode.OK, bookDto)
    }

    suspend fun deleteBook(call: ApplicationCall) {
        val id = call.getIdOrRespondError()
        deleteBookUseCase(id)
        call.respond(HttpStatusCode.NoContent)
    }

    suspend fun updateBookDetails(call: ApplicationCall) {
        val id = call.getIdOrRespondError()
        val bookDetailsUpdatePayload = validateBookUpdate(call.receive<BookUpdateDto>())
        val book = updateBookDetailsUseCase(id, bookDetailsUpdatePayload)
        val bookDto = book.toBookDto(imageBaseUrl)
        call.respond(HttpStatusCode.OK, bookDto)
    }

    suspend fun updateBookCover(call: ApplicationCall) {
        val id = call.getIdOrRespondError()
        val part = call.receiveMultipart().readPart()
        try {
            if (part !is PartData.FileItem || part.name != "cover" || part.originalFileName.isNullOrBlank()) {
                throw ValidationException("File part 'cover' is missing or invalid")
            }
            val book = updateBookCoverUseCase(id, part)
            val bookDto = book.toBookDto(imageBaseUrl)
            call.respond(HttpStatusCode.OK, bookDto)
        } finally {
            part?.dispose?.let { it() }
        }
    }

    private fun ApplicationCall.getIdOrRespondError(): UUID {
        val idParameter = parameters["id"] ?: throw ValidationException("Book ID is missing")
        return try {
            UUID.fromString(idParameter)
        } catch (e: Exception) {
            throw ValidationException("Invalid Book ID format: '$idParameter' is not a valid UUID.")
        }
    }

    private fun validateBookUpdate(bookUpdateDto: BookUpdateDto): BookDetailsUpdatePayload {
        if (bookUpdateDto.title.isBlank()) throw ValidationException("Book title can't be empty.")
        if (bookUpdateDto.title.length > 500) throw ValidationException("Book title can't be longer than 500 characters.")
        if (bookUpdateDto.author.isBlank()) throw ValidationException("Book author can't be empty.")
        if (bookUpdateDto.author.length > 500) throw ValidationException("Book author can't be longer than 500 characters.")
        val status = BookStatus.fromString(bookUpdateDto.status)
            ?: throw ValidationException("Invalid status: ${bookUpdateDto.status}. Allowed values are: ${BookStatus.entries.joinToString()}")
        return BookDetailsUpdatePayload(
            title = bookUpdateDto.title,
            author = bookUpdateDto.author,
            status = status,
            genreIds = bookUpdateDto.genreIds
        )
    }
}