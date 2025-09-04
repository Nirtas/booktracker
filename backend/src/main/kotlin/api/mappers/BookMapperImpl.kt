package ru.jerael.booktracker.backend.api.mappers

import ru.jerael.booktracker.backend.api.dto.book.BookCreationDto
import ru.jerael.booktracker.backend.api.dto.book.BookDto
import ru.jerael.booktracker.backend.api.dto.book.BookUpdateDto
import ru.jerael.booktracker.backend.domain.model.book.Book
import ru.jerael.booktracker.backend.domain.model.book.BookCreationPayload
import ru.jerael.booktracker.backend.domain.model.book.BookDetailsUpdatePayload
import ru.jerael.booktracker.backend.domain.model.book.BookStatus

class BookMapperImpl(private val imageBaseUrl: String) : BookMapper {
    override fun toDto(book: Book): BookDto {
        return book.toBookDto(imageBaseUrl)
    }

    override fun toDto(books: List<Book>): List<BookDto> {
        return books.map { it.toBookDto(imageBaseUrl) }
    }

    override fun toPayload(dto: BookCreationDto): BookCreationPayload {
        return BookCreationPayload(
            title = dto.title,
            author = dto.author,
            coverPath = null,
            status = BookStatus.fromString(dto.status)!!,
            genreIds = dto.genreIds
        )
    }

    override fun toPayload(dto: BookUpdateDto): BookDetailsUpdatePayload {
        return BookDetailsUpdatePayload(
            title = dto.title,
            author = dto.author,
            status = BookStatus.fromString(dto.status)!!,
            genreIds = dto.genreIds
        )
    }
}