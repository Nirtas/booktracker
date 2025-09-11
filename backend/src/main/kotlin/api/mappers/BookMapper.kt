package ru.jerael.booktracker.backend.api.mappers

import ru.jerael.booktracker.backend.api.dto.book.BookCreationDto
import ru.jerael.booktracker.backend.api.dto.book.BookDto
import ru.jerael.booktracker.backend.api.dto.book.BookUpdateDto
import ru.jerael.booktracker.backend.domain.model.book.Book
import ru.jerael.booktracker.backend.domain.model.book.BookCreationPayload
import ru.jerael.booktracker.backend.domain.model.book.BookDetailsUpdatePayload

interface BookMapper {
    fun mapBookToDto(book: Book): BookDto
    fun mapBooksToDtos(books: List<Book>): List<BookDto>
    fun mapCreationDtoToCreationPayload(dto: BookCreationDto): BookCreationPayload
    fun mapUpdateDtoToDetailsUpdatePayload(dto: BookUpdateDto): BookDetailsUpdatePayload
}