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

package data.repository

import kotlinx.coroutines.test.runTest
import org.jetbrains.exposed.v1.exceptions.ExposedSQLException
import org.jetbrains.exposed.v1.jdbc.batchInsert
import org.jetbrains.exposed.v1.jdbc.deleteAll
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.junit.jupiter.api.*
import ru.jerael.booktracker.backend.data.db.tables.BookGenres
import ru.jerael.booktracker.backend.data.db.tables.Books
import ru.jerael.booktracker.backend.data.db.tables.Genres
import ru.jerael.booktracker.backend.data.db.tables.Users
import ru.jerael.booktracker.backend.data.repository.BookRepositoryImpl
import ru.jerael.booktracker.backend.domain.exceptions.BookNotFoundException
import ru.jerael.booktracker.backend.domain.model.book.*
import ru.jerael.booktracker.backend.domain.model.genre.Genre
import ru.jerael.booktracker.backend.domain.model.user.User
import ru.jerael.booktracker.backend.domain.repository.BookRepository
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BookRepositoryImplTest : RepositoryTestBase() {

    private val bookRepository: BookRepository = BookRepositoryImpl()

    private data class BookItem(
        val id: UUID,
        val userId: UUID,
        val title: String,
        val author: String,
        val status: BookStatus,
        val genreIds: List<Int>
    )

    private fun Book.toBookItem(userId: UUID): BookItem = BookItem(
        id = this.id,
        userId = userId,
        title = this.title,
        author = this.author,
        status = this.status,
        genreIds = this.genres.map { it.id }.sorted()
    )

    private val userId = UUID.fromString("02b3d528-a93f-470a-8fb8-7fb622a086fe")
    private val nonExistentUserId = UUID.fromString("344d9af4-075e-491f-9caa-19069bb3789e")

    private val user = User(
        id = userId,
        email = "test@example.com",
        passwordHash = "hash",
        isVerified = true
    )

    private val firstBookId = UUID.fromString("fa9e70f9-ef19-4830-8180-9d185ca0e326")
    private val secondBookId = UUID.fromString("3f252cf7-68f0-4374-b81c-cd01b9471c03")
    private val thirdBookId = UUID.fromString("44ef4d0a-efe0-4dba-9496-e0284345d690")

    private val nonExistentBookId = UUID.fromString("749261f4-3328-4e3d-9c4b-d5cc70a75576")

    private val books = listOf(
        BookItem(
            id = firstBookId,
            userId = userId,
            title = "Title 1",
            author = "Author 1",
            status = BookStatus.WANT_TO_READ,
            genreIds = listOf(1)
        ),
        BookItem(
            id = secondBookId,
            userId = userId,
            title = "Title 2",
            author = "Author 2",
            status = BookStatus.READING,
            genreIds = listOf(2, 3)
        ),
        BookItem(
            id = thirdBookId,
            userId = userId,
            title = "Title 3",
            author = "Author 3",
            status = BookStatus.READ,
            genreIds = emptyList()
        )
    )

    private data class GenreItem(
        val id: Int,
        val nameEn: String,
        val nameRu: String
    )

    private val genres = listOf(
        GenreItem(id = 1, nameEn = "gaming", nameRu = "игры"),
        GenreItem(id = 2, nameEn = "adventure", nameRu = "приключения"),
        GenreItem(id = 3, nameEn = "science fiction", nameRu = "научная фантастика")
    )

    private val imageBaseUrl = "http://storage.com"

    @BeforeEach
    fun setUp() {
        transaction {
            BookGenres.deleteAll()
            Books.deleteAll()
            Genres.deleteAll()
            Users.deleteAll()
            Users.insert {
                it[Users.id] = user.id
                it[Users.email] = user.email
                it[Users.passwordHash] = user.passwordHash
                it[Users.isVerified] = user.isVerified
            }
            Genres.batchInsert(genres) { genre ->
                this[Genres.id] = genre.id
                this[Genres.nameEn] = genre.nameEn
                this[Genres.nameRu] = genre.nameRu
            }
            Books.batchInsert(books) { book ->
                this[Books.id] = book.id
                this[Books.userId] = book.userId
                this[Books.title] = book.title
                this[Books.author] = book.author
                this[Books.status] = book.status
            }
            val bookGenres = books.flatMap { book ->
                book.genreIds.map { genreId ->
                    book.id to genreId
                }
            }
            BookGenres.batchInsert(bookGenres) { (bookId, genreId) ->
                this[BookGenres.bookId] = bookId
                this[BookGenres.genreId] = genreId
            }
        }
    }

    @Test
    fun `when getBooks is called, it should return a list of all user's books with their genres`() = runTest {
        val result = bookRepository.getBooks(userId, "en")
        assertEquals(books.size, result.size)

        val expectedBookItems = books.map { it.copy(genreIds = it.genreIds.sorted()) }.toSet()
        val actualBookItems = result.map { it.toBookItem(userId) }.toSet()
        assertEquals(expectedBookItems, actualBookItems)
    }

    @Test
    fun `when addBook is called with valid data and genres, it should create a book and the link in BookGenres table`() =
        runTest {
            val addBookData = AddBookData(
                userId = userId,
                title = "Title 4",
                author = "Author 4",
                coverUrl = null,
                status = BookStatus.READ,
                genreIds = listOf(1)
            )

            val createdBook = bookRepository.addBook(addBookData, "en")

            assertNotNull(createdBook.id)

            val expectedGenres = addBookData.genreIds
                .map { genreId ->
                    val genreData = genres.first { it.id == genreId }
                    Genre(id = genreData.id, name = genreData.nameEn)
                }
                .sortedBy { it.name }

            val expectedBook = Book(
                id = createdBook.id,
                title = addBookData.title,
                author = addBookData.author,
                coverUrl = addBookData.coverUrl,
                status = addBookData.status,
                createdAt = createdBook.createdAt,
                genres = expectedGenres
            )

            assertEquals(expectedBook, createdBook)
            val allBooks = bookRepository.getBooks(userId, "en")
            assertEquals(books.size + 1, allBooks.size)
        }

    @Test
    fun `when addBook is called with valid data and zero genres, it should create a book with an empty genre list`() =
        runTest {
            val addBookData = AddBookData(
                userId = userId,
                title = "Title 4",
                author = "Author 4",
                coverUrl = null,
                status = BookStatus.READ,
                genreIds = emptyList()
            )

            val createdBook = bookRepository.addBook(addBookData, "en")

            assertNotNull(createdBook.id)

            val expectedBook = Book(
                id = createdBook.id,
                title = addBookData.title,
                author = addBookData.author,
                coverUrl = addBookData.coverUrl,
                status = addBookData.status,
                createdAt = createdBook.createdAt,
                genres = emptyList()
            )

            assertEquals(expectedBook, createdBook)
            val allBooks = bookRepository.getBooks(userId, "en")
            assertEquals(books.size + 1, allBooks.size)
        }

    @Test
    fun `when addBook is called with a non-existent genre ID, an ExposedSQLException for foreign key violation should be thrown`() =
        runTest {
            val addBookData = AddBookData(
                userId = userId,
                title = "Title 4",
                author = "Author 4",
                coverUrl = null,
                status = BookStatus.READ,
                genreIds = listOf(5)
            )

            val exception = assertThrows<ExposedSQLException> {
                bookRepository.addBook(addBookData, "en")
            }

            assertTrue(exception.message?.contains("violates foreign key constraint") == true)
            val allBooks = bookRepository.getBooks(userId, "en")
            assertEquals(books.size, allBooks.size)
        }

    @Test
    fun `when getBookById is called for a book with multiple genres using 'en' language, it should return the correct book with English genre names`() =
        runTest {
            val expectedBookItem = books.find { it.id == secondBookId }!!
            val expectedBookGenres = genres
                .filter { it.id in expectedBookItem.genreIds }
                .map { Genre(id = it.id, name = it.nameEn) }
                .sortedBy { it.name }

            val result = bookRepository.getBookById(userId, secondBookId, "en")
            assertNotNull(result)
            val resultAsBookItem = result.toBookItem(userId)
            assertEquals(expectedBookItem.copy(genreIds = expectedBookItem.genreIds.sorted()), resultAsBookItem)
            val resultGenres = result.genres.sortedBy { it.name }
            assertEquals(expectedBookGenres, resultGenres)
        }

    @Test
    fun `when getBookById is called for a book with multiple genres using 'ru' language, it should return the correct book with Russian genre names`() =
        runTest {
            val expectedBookItem = books.find { it.id == secondBookId }!!
            val expectedBookGenres = genres
                .filter { it.id in expectedBookItem.genreIds }
                .map { Genre(id = it.id, name = it.nameRu) }
                .sortedBy { it.name }

            val result = bookRepository.getBookById(userId, secondBookId, "ru")

            assertNotNull(result)
            val resultAsBookItem = result.toBookItem(userId)
            assertEquals(expectedBookItem.copy(genreIds = expectedBookItem.genreIds.sorted()), resultAsBookItem)
            val resultGenres = result.genres.sortedBy { it.name }
            assertEquals(expectedBookGenres, resultGenres)
        }

    @Test
    fun `when getBookById is called for a book with no genres, it should return the book with an empty genre list`() =
        runTest {
            val expectedBookItem = books.find { it.id == thirdBookId }!!

            val result = bookRepository.getBookById(userId, thirdBookId, "en")

            assertNotNull(result)
            val resultAsBookItem = result.toBookItem(userId)
            assertEquals(expectedBookItem.copy(genreIds = expectedBookItem.genreIds.sorted()), resultAsBookItem)
        }

    @Test
    fun `when getBookById is called with a non-existent ID, it should return null`() = runTest {
        val result = bookRepository.getBookById(userId, nonExistentBookId, "en")

        assertNull(result)
    }

    @Test
    fun `when updateBookDetails is called with new text fields and a new set of genres, it should update the book and correctly replace all genre links`() =
        runTest {
            val updateBookDetailsData = UpdateBookDetailsData(
                userId = userId,
                bookId = firstBookId,
                title = "Title 4",
                author = "Author 4",
                status = BookStatus.READ,
                genreIds = listOf(2)
            )

            val updatedBook = bookRepository.updateBookDetails(updateBookDetailsData, "en")

            val book = Book(
                id = firstBookId,
                title = updateBookDetailsData.title,
                author = updateBookDetailsData.author,
                coverUrl = null,
                status = updateBookDetailsData.status,
                createdAt = updatedBook.createdAt,
                genres = genres
                    .filter { it.id in updateBookDetailsData.genreIds }
                    .map { Genre(id = it.id, name = it.nameEn) }
                    .sortedBy { it.name }
            )

            assertEquals(book, updatedBook)
            val bookFromDb = bookRepository.getBookById(userId, updatedBook.id, "en")
            assertNotNull(bookFromDb)
            assertEquals(book, bookFromDb)
        }

    @Test
    fun `when updateBookDetails is called with an empty genre list, it should update the book and remove all its genre links`() =
        runTest {
            val updateBookDetailsData = UpdateBookDetailsData(
                userId = userId,
                bookId = firstBookId,
                title = "Title 4",
                author = "Author 4",
                status = BookStatus.READ,
                genreIds = emptyList()
            )

            val updatedBook = bookRepository.updateBookDetails(updateBookDetailsData, "en")

            val book = Book(
                id = firstBookId,
                title = updateBookDetailsData.title,
                author = updateBookDetailsData.author,
                coverUrl = null,
                status = updateBookDetailsData.status,
                createdAt = updatedBook.createdAt,
                genres = emptyList()
            )

            assertEquals(book, updatedBook)
            val bookFromDb = bookRepository.getBookById(userId, updatedBook.id, "en")
            assertNotNull(bookFromDb)
            assertEquals(book, bookFromDb)
        }

    @Test
    fun `when updateBookDetails is called to add genres to a book that had none, it should update the book and create new genre links`() =
        runTest {
            val updateBookDetailsData = UpdateBookDetailsData(
                userId = userId,
                bookId = thirdBookId,
                title = "Title 4",
                author = "Author 4",
                status = BookStatus.READ,
                genreIds = listOf(2)
            )

            val updatedBook = bookRepository.updateBookDetails(updateBookDetailsData, "en")

            val book = Book(
                id = thirdBookId,
                title = updateBookDetailsData.title,
                author = updateBookDetailsData.author,
                coverUrl = null,
                status = updateBookDetailsData.status,
                createdAt = updatedBook.createdAt,
                genres = genres
                    .filter { it.id in updateBookDetailsData.genreIds }
                    .map { Genre(id = it.id, name = it.nameEn) }
                    .sortedBy { it.name }
            )

            assertEquals(book, updatedBook)
            val bookFromDb = bookRepository.getBookById(userId, updatedBook.id, "en")
            assertNotNull(bookFromDb)
            assertEquals(book, bookFromDb)
        }

    @Test
    fun `when updateBookDetails is called for a non-existent book ID, a BookNotFoundException should be thrown`() =
        runTest {
            val updateBookDetailsData = UpdateBookDetailsData(
                userId = nonExistentUserId,
                bookId = firstBookId,
                title = "Title 4",
                author = "Author 4",
                status = BookStatus.READ,
                genreIds = listOf(2)
            )

            val exception = assertThrows<BookNotFoundException> {
                bookRepository.updateBookDetails(updateBookDetailsData, "en")
            }

            assertTrue(exception.message!!.contains(firstBookId.toString()))
        }

    @Test
    fun `when updateBookDetails is called with a non-existent genre ID, an ExposedSQLException for foreign key violation should be thrown`() =
        runTest {
            val updateBookDetailsData = UpdateBookDetailsData(
                userId = userId,
                bookId = firstBookId,
                title = "Title 4",
                author = "Author 4",
                status = BookStatus.READ,
                genreIds = listOf(5)
            )

            val exception = assertThrows<ExposedSQLException> {
                bookRepository.updateBookDetails(updateBookDetailsData, "en")
            }

            assertTrue(exception.message?.contains("violates foreign key constraint") == true)
        }

    @Test
    fun `when updateBookCover is called with a valid book ID and a new path, it should update only the coverPath field of the book`() =
        runTest {
            val newCoverPath = "$imageBaseUrl/$userId/covers/book.jpg"

            val bookBeforeUpdate = bookRepository.getBookById(userId, firstBookId, "en")

            assertNotNull(bookBeforeUpdate)
            assertNull(bookBeforeUpdate.coverUrl)

            val updateBookCoverData = UpdateBookCoverData(
                userId = userId,
                bookId = firstBookId,
                coverUrl = newCoverPath
            )
            val updatedBook = bookRepository.updateBookCover(updateBookCoverData, "en")

            assertEquals(newCoverPath, updatedBook.coverUrl)
            val expectedBook = bookBeforeUpdate.copy(coverUrl = newCoverPath)
            assertEquals(updatedBook, expectedBook)
            val bookFromDb = bookRepository.getBookById(userId, updatedBook.id, "en")
            assertEquals(expectedBook, bookFromDb)
        }

    @Test
    fun `when updateBookCover is called for a non-existent book ID, a BookNotFoundException should be thrown`() =
        runTest {
            val newCoverPath = "$imageBaseUrl/$nonExistentBookId/covers/book.jpg"
            val updateBookCoverData = UpdateBookCoverData(
                userId = nonExistentUserId,
                bookId = firstBookId,
                coverUrl = newCoverPath
            )

            val exception = assertThrows<BookNotFoundException> {
                bookRepository.updateBookCover(updateBookCoverData, "en")
            }

            assertTrue(exception.message!!.contains(firstBookId.toString()))
        }

    @Test
    fun `when deleteBook is called with an existing book ID, it should delete the book and its links from BookGenres table and return true`() =
        runTest {
            assertDoesNotThrow {
                bookRepository.deleteBook(userId, firstBookId)
            }

            val result = bookRepository.getBookById(userId, firstBookId, "en")
            assertNull(result)

            val linksCount = transaction { BookGenres.selectAll().where { BookGenres.bookId eq firstBookId }.count() }
            assertEquals(0, linksCount)
        }

    @Test
    fun `when deleteBook is called with a non-existent book ID, a BookNotFoundException should be thrown`() = runTest {
        assertThrows<BookNotFoundException> {
            bookRepository.deleteBook(userId, nonExistentBookId)
        }
    }
}