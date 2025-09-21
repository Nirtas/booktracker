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
import ru.jerael.booktracker.backend.api.util.getUuidFromPath
import ru.jerael.booktracker.backend.api.util.language
import ru.jerael.booktracker.backend.api.validation.validator.BookValidator
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
        validator.validateCreation(request.bookCreationDto)
        val bookCreationPayload = bookMapper.mapCreationDtoToCreationPayload(request.bookCreationDto)
        val newBook = addBookUseCase(bookCreationPayload, request.coverBytes, request.coverFileName, language)
        call.respond(HttpStatusCode.Created, bookMapper.mapBookToDto(newBook))
    }

    suspend fun getBookById(call: ApplicationCall) {
        val language = call.request.language()
        val id = call.getUuidFromPath("id")
        val book = getBookByIdUseCase(id, language)
        call.respond(HttpStatusCode.OK, bookMapper.mapBookToDto(book))
    }

    suspend fun deleteBook(call: ApplicationCall) {
        val id = call.getUuidFromPath("id")
        deleteBookUseCase(id)
        call.respond(HttpStatusCode.NoContent)
    }

    suspend fun updateBookDetails(call: ApplicationCall) {
        val language = call.request.language()
        val id = call.getUuidFromPath("id")
        val bookUpdateDto = call.receive<BookUpdateDto>()
        validator.validateUpdate(bookUpdateDto)
        val bookDetailsUpdatePayload = bookMapper.mapUpdateDtoToDetailsUpdatePayload(bookUpdateDto)
        val book = updateBookDetailsUseCase(id, bookDetailsUpdatePayload, language)
        call.respond(HttpStatusCode.OK, bookMapper.mapBookToDto(book))
    }

    suspend fun updateBookCover(call: ApplicationCall) {
        val language = call.request.language()
        val id = call.getUuidFromPath("id")
        val request = multipartParser.parseBookCoverUpdate(call)
        val book = updateBookCoverUseCase(id, request.coverBytes, request.coverFileName, language)
        call.respond(HttpStatusCode.OK, bookMapper.mapBookToDto(book))
    }
}