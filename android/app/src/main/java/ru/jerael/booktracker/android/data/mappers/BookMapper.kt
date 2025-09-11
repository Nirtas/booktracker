package ru.jerael.booktracker.android.data.mappers

import ru.jerael.booktracker.android.data.local.entity.BookEntity
import ru.jerael.booktracker.android.data.local.relations.BookWithGenres
import ru.jerael.booktracker.android.data.remote.dto.book.BookDto
import ru.jerael.booktracker.android.domain.model.book.Book

interface BookMapper {
    fun mapEntityToBook(entity: BookEntity): Book
    fun mapEntitiesToBooks(entities: List<BookEntity>): List<Book>
    fun mapDtoToEntity(dto: BookDto): BookEntity
    fun mapDtosToEntities(dtos: List<BookDto>): List<BookEntity>
    fun mapBookWithGenresToBook(bookWithGenres: BookWithGenres): Book
    fun mapBooksWithGenresToBooks(booksWithGenres: List<BookWithGenres>): List<Book>
}