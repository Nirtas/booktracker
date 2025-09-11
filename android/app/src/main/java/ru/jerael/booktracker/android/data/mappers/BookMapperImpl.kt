package ru.jerael.booktracker.android.data.mappers

import ru.jerael.booktracker.android.data.local.entity.BookEntity
import ru.jerael.booktracker.android.data.local.relations.BookWithGenres
import ru.jerael.booktracker.android.data.remote.dto.book.BookDto
import ru.jerael.booktracker.android.domain.model.book.Book
import ru.jerael.booktracker.android.domain.model.book.BookStatus
import java.time.Instant
import javax.inject.Inject

class BookMapperImpl @Inject constructor(
    private val genreMapper: GenreMapper
) : BookMapper {
    override fun mapEntityToBook(entity: BookEntity): Book {
        return Book(
            id = entity.id,
            title = entity.title,
            author = entity.author,
            coverUrl = entity.coverUrl,
            status = BookStatus.fromString(entity.status) ?: BookStatus.WANT_TO_READ,
            createdAt = Instant.ofEpochMilli(entity.createdAt),
            genres = emptyList()
        )
    }

    override fun mapEntitiesToBooks(entities: List<BookEntity>): List<Book> {
        return entities.map { mapEntityToBook(it) }
    }

    override fun mapDtoToEntity(dto: BookDto): BookEntity {
        return BookEntity(
            id = dto.id,
            title = dto.title,
            author = dto.author,
            coverUrl = dto.coverUrl,
            status = dto.status,
            createdAt = dto.createdAt
        )
    }

    override fun mapDtosToEntities(dtos: List<BookDto>): List<BookEntity> {
        return dtos.map { mapDtoToEntity(it) }
    }

    override fun mapBookWithGenresToBook(bookWithGenres: BookWithGenres): Book {
        return mapEntityToBook(bookWithGenres.book).copy(
            genres = genreMapper.mapEntitiesToGenres(bookWithGenres.genres)
        )
    }

    override fun mapBooksWithGenresToBooks(booksWithGenres: List<BookWithGenres>): List<Book> {
        return booksWithGenres.map { mapBookWithGenresToBook(it) }
    }
}