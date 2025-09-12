package ru.jerael.booktracker.backend.api.controller

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import ru.jerael.booktracker.backend.api.dto.book.BookUpdateDto
import ru.jerael.booktracker.backend.api.mappers.BookMapper
import ru.jerael.booktracker.backend.api.parsing.MultipartParser
import ru.jerael.booktracker.backend.api.util.language
import ru.jerael.booktracker.backend.api.validation.BookValidator
import ru.jerael.booktracker.backend.domain.usecases.book.*

class BookController(
    private val getBooksUseCase: GetBooksUseCase,
    private val addBookUseCase: AddBookUseCase,
    private val getBookByIdUseCase: GetBookByIdUseCase,
    private val updateBookDetailsUseCase: UpdateBookDetailsUseCase,
    private val updateBookCoverUseCase: UpdateBookCoverUseCase,
    private val deleteBookUseCase: DeleteBookUseCase,
    private val validator: BookValidator,
    private val multipartParser: MultipartParser,
    private val bookMapper: BookMapper
) {

    suspend fun getAllBooks(call: ApplicationCall) {
        val language = call.request.language()
        val books = getBooksUseCase(language)
        call.respond(HttpStatusCode.OK, bookMapper.mapBooksToDtos(books))
    }

    suspend fun addBook(call: ApplicationCall) {
        val language = call.request.language()
        val request = multipartParser.parseBookCreation(call)
        val bookCreationPayload = validator.validateCreation(request.bookCreationDto)
        val newBook = addBookUseCase(bookCreationPayload, request.coverBytes, request.coverFileName, language)
        call.respond(HttpStatusCode.Created, bookMapper.mapBookToDto(newBook))
    }

    suspend fun getBookById(call: ApplicationCall) {
        val language = call.request.language()
        val id = validator.validateId(call.parameters["id"])
        val book = getBookByIdUseCase(id, language)
        call.respond(HttpStatusCode.OK, bookMapper.mapBookToDto(book))
    }

    suspend fun deleteBook(call: ApplicationCall) {
        val id = validator.validateId(call.parameters["id"])
        deleteBookUseCase(id)
        call.respond(HttpStatusCode.NoContent)
    }

    suspend fun updateBookDetails(call: ApplicationCall) {
        val language = call.request.language()
        val id = validator.validateId(call.parameters["id"])
        val bookUpdateDto = call.receive<BookUpdateDto>()
        val bookDetailsUpdatePayload = validator.validateUpdate(bookUpdateDto)
        val book = updateBookDetailsUseCase(id, bookDetailsUpdatePayload, language)
        call.respond(HttpStatusCode.OK, bookMapper.mapBookToDto(book))
    }

    suspend fun updateBookCover(call: ApplicationCall) {
        val language = call.request.language()
        val id = validator.validateId(call.parameters["id"])
        val request = multipartParser.parseBookCoverUpdate(call)
        val book = updateBookCoverUseCase(id, request.coverBytes, request.coverFileName, language)
        call.respond(HttpStatusCode.OK, bookMapper.mapBookToDto(book))
    }
}