package ru.jerael.booktracker.backend.api.mappers

import ru.jerael.booktracker.backend.api.dto.book.BookCreationDto
import ru.jerael.booktracker.backend.api.dto.book.BookDto
import ru.jerael.booktracker.backend.api.dto.book.BookUpdateDto
import ru.jerael.booktracker.backend.domain.model.book.Book
import ru.jerael.booktracker.backend.domain.model.book.BookCreationPayload
import ru.jerael.booktracker.backend.domain.model.book.BookDetailsUpdatePayload
import ru.jerael.booktracker.backend.domain.model.book.BookStatus

class BookMapperImpl(
    private val imageBaseUrl: String,
    private val genreMapper: GenreMapper
) : BookMapper {
    override fun mapBookToDto(book: Book): BookDto {
        return BookDto(
            id = book.id.toString(),
            title = book.title,
            author = book.author,
            coverUrl = book.coverPath?.let { "$imageBaseUrl/$it" },
            status = book.status.value,
            createdAt = book.createdAt.toEpochMilli(),
            genres = genreMapper.mapGenresToDtos(book.genres)
        )
    }

    override fun mapBooksToDtos(books: List<Book>): List<BookDto> {
        return books.map { mapBookToDto(it) }
    }

    override fun mapCreationDtoToCreationPayload(dto: BookCreationDto): BookCreationPayload {
        return BookCreationPayload(
            title = dto.title,
            author = dto.author,
            coverPath = null,
            status = BookStatus.fromString(dto.status)!!,
            genreIds = dto.genreIds
        )
    }

    override fun mapUpdateDtoToDetailsUpdatePayload(dto: BookUpdateDto): BookDetailsUpdatePayload {
        return BookDetailsUpdatePayload(
            title = dto.title,
            author = dto.author,
            status = BookStatus.fromString(dto.status)!!,
            genreIds = dto.genreIds
        )
    }
}