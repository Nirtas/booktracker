/*
 * BookTracker is a full-stack application for tracking your reading list.
 * Copyright (C) 2025  Jerael (https://github.com/Nirtas)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package ru.jerael.booktracker.backend.api.controller

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import ru.jerael.booktracker.backend.api.dto.book.BookUpdateDto
import ru.jerael.booktracker.backend.api.mappers.BookMapper
import ru.jerael.booktracker.backend.api.parsing.MultipartParser
import ru.jerael.booktracker.backend.api.util.language
import ru.jerael.booktracker.backend.api.validation.validator.BookValidator
import ru.jerael.booktracker.backend.domain.model.book.BookCoverUpdatePayload
import ru.jerael.booktracker.backend.domain.model.book.BookCreationPayload
import ru.jerael.booktracker.backend.domain.model.book.BookDetailsUpdatePayload
import ru.jerael.booktracker.backend.domain.model.book.BookStatus
import ru.jerael.booktracker.backend.domain.usecases.book.*
import java.util.*

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
    suspend fun getAllBooks(call: ApplicationCall, userId: UUID) {
        val language = call.request.language()
        val books = getBooksUseCase(userId, language)
        call.respond(HttpStatusCode.OK, bookMapper.mapBooksToDtos(books))
    }

    suspend fun addBook(call: ApplicationCall, userId: UUID) {
        val language = call.request.language()
        val request = multipartParser.parseBookCreation(call)
        validator.validateCreation(request.bookCreationDto)
        val bookCreationPayload = BookCreationPayload(
            userId = userId,
            language = language,
            title = request.bookCreationDto.title,
            author = request.bookCreationDto.author,
            coverBytes = request.coverBytes,
            coverFileName = request.coverFileName,
            status = BookStatus.fromString(request.bookCreationDto.status)!!,
            genreIds = request.bookCreationDto.genreIds
        )
        val newBook = addBookUseCase(bookCreationPayload)
        call.respond(HttpStatusCode.Created, bookMapper.mapBookToDto(newBook))
    }

    suspend fun getBookById(call: ApplicationCall, userId: UUID, bookId: UUID) {
        val language = call.request.language()
        val book = getBookByIdUseCase(userId, bookId, language)
        call.respond(HttpStatusCode.OK, bookMapper.mapBookToDto(book))
    }

    suspend fun deleteBook(call: ApplicationCall, userId: UUID, bookId: UUID) {
        deleteBookUseCase(userId, bookId)
        call.respond(HttpStatusCode.NoContent)
    }

    suspend fun updateBookDetails(call: ApplicationCall, userId: UUID, bookId: UUID) {
        val language = call.request.language()
        val bookUpdateDto = call.receive<BookUpdateDto>()
        validator.validateUpdate(bookUpdateDto)
        val bookDetailsUpdatePayload = BookDetailsUpdatePayload(
            userId = userId,
            bookId = bookId,
            language = language,
            title = bookUpdateDto.title,
            author = bookUpdateDto.author,
            status = BookStatus.fromString(bookUpdateDto.status)!!,
            genreIds = bookUpdateDto.genreIds
        )
        val book = updateBookDetailsUseCase(bookDetailsUpdatePayload)
        call.respond(HttpStatusCode.OK, bookMapper.mapBookToDto(book))
    }

    suspend fun updateBookCover(call: ApplicationCall, userId: UUID, bookId: UUID) {
        val language = call.request.language()
        val request = multipartParser.parseBookCoverUpdate(call)
        val bookCoverUpdatePayload = BookCoverUpdatePayload(
            userId = userId,
            bookId = bookId,
            language = language,
            coverBytes = request.coverBytes,
            coverFileName = request.coverFileName
        )
        val book = updateBookCoverUseCase(bookCoverUpdatePayload)
        call.respond(HttpStatusCode.OK, bookMapper.mapBookToDto(book))
    }
}