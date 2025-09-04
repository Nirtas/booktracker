package data.repository

import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.v1.exceptions.ExposedSQLException
import org.jetbrains.exposed.v1.jdbc.batchInsert
import org.jetbrains.exposed.v1.jdbc.deleteAll
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertFalse
import ru.jerael.booktracker.backend.data.db.tables.BookGenres
import ru.jerael.booktracker.backend.data.db.tables.Books
import ru.jerael.booktracker.backend.data.db.tables.Genres
import ru.jerael.booktracker.backend.data.repository.BookRepositoryImpl
import ru.jerael.booktracker.backend.domain.exceptions.BookNotFoundException
import ru.jerael.booktracker.backend.domain.model.book.Book
import ru.jerael.booktracker.backend.domain.model.book.BookCreationPayload
import ru.jerael.booktracker.backend.domain.model.book.BookDetailsUpdatePayload
import ru.jerael.booktracker.backend.domain.model.book.BookStatus
import ru.jerael.booktracker.backend.domain.model.genre.Genre
import ru.jerael.booktracker.backend.domain.repository.BookRepository
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BookRepositoryImplTest : RepositoryTestBase() {

    private val bookRepository: BookRepository = BookRepositoryImpl()

    private data class BookItem(
        val id: UUID,
        val title: String,
        val author: String,
        val status: BookStatus,
        val genreIds: List<Int>
    )

    private fun Book.toBookItem(): BookItem = BookItem(
        id = this.id,
        title = this.title,
        author = this.author,
        status = this.status,
        genreIds = this.genres.map { it.id }.sorted()
    )

    private val firstBookId = UUID.fromString("fa9e70f9-ef19-4830-8180-9d185ca0e326")
    private val secondBookId = UUID.fromString("3f252cf7-68f0-4374-b81c-cd01b9471c03")
    private val thirdBookId = UUID.fromString("44ef4d0a-efe0-4dba-9496-e0284345d690")

    private val nonExistentBookId = UUID.fromString("749261f4-3328-4e3d-9c4b-d5cc70a75576")

    private val books = listOf(
        BookItem(
            id = firstBookId,
            title = "Title 1",
            author = "Author 1",
            status = BookStatus.WANT_TO_READ,
            genreIds = listOf(1)
        ),
        BookItem(
            id = secondBookId,
            title = "Title 2",
            author = "Author 2",
            status = BookStatus.READING,
            genreIds = listOf(2, 3)
        ),
        BookItem(
            id = thirdBookId,
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

    @BeforeEach
    fun setUp() {
        transaction {
            BookGenres.deleteAll()
            Books.deleteAll()
            Genres.deleteAll()
            Genres.batchInsert(genres) { genre ->
                this[Genres.id] = genre.id
                this[Genres.nameEn] = genre.nameEn
                this[Genres.nameRu] = genre.nameRu
            }
            Books.batchInsert(books) { book ->
                this[Books.id] = book.id
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
    fun `when getBooks is called, it should return a list of all books with their genres`() = runBlocking {
        val result = bookRepository.getBooks("en")
        assertEquals(books.size, result.size)

        val expectedBookItems = books.map { it.copy(genreIds = it.genreIds.sorted()) }.toSet()
        val actualBookItems = result.map { it.toBookItem() }.toSet()
        assertEquals(expectedBookItems, actualBookItems)
    }

    @Test
    fun `when addBook is called with valid data and genres, it should create a book and the link in BookGenres table`() =
        runBlocking {
            val bookCreationPayload = BookCreationPayload(
                title = "Title 4",
                author = "Author 4",
                coverPath = null,
                status = BookStatus.READ,
                genreIds = listOf(1)
            )

            val createdBook = bookRepository.addBook(bookCreationPayload, "en")

            assertNotNull(createdBook.id)

            val expectedGenres = bookCreationPayload.genreIds
                .map { genreId ->
                    val genreData = genres.first { it.id == genreId }
                    Genre(id = genreData.id, name = genreData.nameEn)
                }
                .sortedBy { it.name }

            val expectedBook = Book(
                id = createdBook.id,
                title = bookCreationPayload.title,
                author = bookCreationPayload.author,
                coverPath = bookCreationPayload.coverPath,
                status = bookCreationPayload.status,
                createdAt = createdBook.createdAt,
                genres = expectedGenres
            )

            assertEquals(expectedBook, createdBook)
            val allBooks = bookRepository.getBooks("en")
            assertEquals(books.size + 1, allBooks.size)
        }

    @Test
    fun `when addBook is called with valid data and zero genres, it should create a book with an empty genre list`() =
        runBlocking {
            val bookCreationPayload = BookCreationPayload(
                title = "Title 4",
                author = "Author 4",
                coverPath = null,
                status = BookStatus.READ,
                genreIds = emptyList()
            )

            val createdBook = bookRepository.addBook(bookCreationPayload, "en")

            assertNotNull(createdBook.id)

            val expectedBook = Book(
                id = createdBook.id,
                title = bookCreationPayload.title,
                author = bookCreationPayload.author,
                coverPath = bookCreationPayload.coverPath,
                status = bookCreationPayload.status,
                createdAt = createdBook.createdAt,
                genres = emptyList()
            )

            assertEquals(expectedBook, createdBook)
            val allBooks = bookRepository.getBooks("en")
            assertEquals(books.size + 1, allBooks.size)
        }

    @Test
    fun `when addBook is called with a non-existent genre ID, an ExposedSQLException for foreign key violation should be thrown`() =
        runBlocking {
            val bookCreationPayload = BookCreationPayload(
                title = "Title 4",
                author = "Author 4",
                coverPath = null,
                status = BookStatus.READ,
                genreIds = listOf(5)
            )

            val exception = assertThrows<ExposedSQLException> {
                bookRepository.addBook(bookCreationPayload, "en")
            }

            assertTrue(exception.message?.contains("violates foreign key constraint") == true)
            val allBooks = bookRepository.getBooks("en")
            assertEquals(books.size, allBooks.size)
        }

    @Test
    fun `when getBookById is called for a book with multiple genres using 'en' language, it should return the correct book with English genre names`() =
        runBlocking {
            val expectedBookItem = books.find { it.id == secondBookId }!!
            val expectedBookGenres = genres
                .filter { it.id in expectedBookItem.genreIds }
                .map { Genre(id = it.id, name = it.nameEn) }
                .sortedBy { it.name }

            val result = bookRepository.getBookById(secondBookId, "en")
            assertNotNull(result)
            val resultAsBookItem = result.toBookItem()
            assertEquals(expectedBookItem.copy(genreIds = expectedBookItem.genreIds.sorted()), resultAsBookItem)
            val resultGenres = result.genres.sortedBy { it.name }
            assertEquals(expectedBookGenres, resultGenres)
        }

    @Test
    fun `when getBookById is called for a book with multiple genres using 'ru' language, it should return the correct book with Russian genre names`() =
        runBlocking {
            val expectedBookItem = books.find { it.id == secondBookId }!!
            val expectedBookGenres = genres
                .filter { it.id in expectedBookItem.genreIds }
                .map { Genre(id = it.id, name = it.nameRu) }
                .sortedBy { it.name }

            val result = bookRepository.getBookById(secondBookId, "ru")

            assertNotNull(result)
            val resultAsBookItem = result.toBookItem()
            assertEquals(expectedBookItem.copy(genreIds = expectedBookItem.genreIds.sorted()), resultAsBookItem)
            val resultGenres = result.genres.sortedBy { it.name }
            assertEquals(expectedBookGenres, resultGenres)
        }

    @Test
    fun `when getBookById is called for a book with no genres, it should return the book with an empty genre list`() =
        runBlocking {
            val expectedBookItem = books.find { it.id == thirdBookId }!!

            val result = bookRepository.getBookById(thirdBookId, "en")

            assertNotNull(result)
            val resultAsBookItem = result.toBookItem()
            assertEquals(expectedBookItem.copy(genreIds = expectedBookItem.genreIds.sorted()), resultAsBookItem)
        }

    @Test
    fun `when getBookById is called with a non-existent ID, it should return null`() = runBlocking {
        val result = bookRepository.getBookById(nonExistentBookId, "en")

        assertNull(result)
    }

    @Test
    fun `when updateBookDetails is called with new text fields and a new set of genres, it should update the book and correctly replace all genre links`() =
        runBlocking {
            val bookDetailsUpdatePayload = BookDetailsUpdatePayload(
                title = "Title 4",
                author = "Author 4",
                status = BookStatus.READ,
                genreIds = listOf(2)
            )

            val updatedBook = bookRepository.updateBookDetails(firstBookId, bookDetailsUpdatePayload, "en")

            val book = Book(
                id = firstBookId,
                title = bookDetailsUpdatePayload.title,
                author = bookDetailsUpdatePayload.author,
                coverPath = null,
                status = bookDetailsUpdatePayload.status,
                createdAt = updatedBook.createdAt,
                genres = genres
                    .filter { it.id in bookDetailsUpdatePayload.genreIds }
                    .map { Genre(id = it.id, name = it.nameEn) }
                    .sortedBy { it.name }
            )

            assertEquals(book, updatedBook)
            val bookFromDb = bookRepository.getBookById(updatedBook.id, "en")
            assertNotNull(bookFromDb)
            assertEquals(book, bookFromDb)
        }

    @Test
    fun `when updateBookDetails is called with an empty genre list, it should update the book and remove all its genre links`() =
        runBlocking {
            val bookDetailsUpdatePayload = BookDetailsUpdatePayload(
                title = "Title 4",
                author = "Author 4",
                status = BookStatus.READ,
                genreIds = emptyList()
            )

            val updatedBook = bookRepository.updateBookDetails(firstBookId, bookDetailsUpdatePayload, "en")

            val book = Book(
                id = firstBookId,
                title = bookDetailsUpdatePayload.title,
                author = bookDetailsUpdatePayload.author,
                coverPath = null,
                status = bookDetailsUpdatePayload.status,
                createdAt = updatedBook.createdAt,
                genres = emptyList()
            )

            assertEquals(book, updatedBook)
            val bookFromDb = bookRepository.getBookById(updatedBook.id, "en")
            assertNotNull(bookFromDb)
            assertEquals(book, bookFromDb)
        }

    @Test
    fun `when updateBookDetails is called to add genres to a book that had none, it should update the book and create new genre links`() =
        runBlocking {
            val bookDetailsUpdatePayload = BookDetailsUpdatePayload(
                title = "Title 4",
                author = "Author 4",
                status = BookStatus.READ,
                genreIds = listOf(2)
            )

            val updatedBook = bookRepository.updateBookDetails(thirdBookId, bookDetailsUpdatePayload, "en")

            val book = Book(
                id = thirdBookId,
                title = bookDetailsUpdatePayload.title,
                author = bookDetailsUpdatePayload.author,
                coverPath = null,
                status = bookDetailsUpdatePayload.status,
                createdAt = updatedBook.createdAt,
                genres = genres
                    .filter { it.id in bookDetailsUpdatePayload.genreIds }
                    .map { Genre(id = it.id, name = it.nameEn) }
                    .sortedBy { it.name }
            )

            assertEquals(book, updatedBook)
            val bookFromDb = bookRepository.getBookById(updatedBook.id, "en")
            assertNotNull(bookFromDb)
            assertEquals(book, bookFromDb)
        }

    @Test
    fun `when updateBookDetails is called for a non-existent book ID, a BookNotFoundException should be thrown`() =
        runBlocking {
            val bookDetailsUpdatePayload = BookDetailsUpdatePayload(
                title = "Title 4",
                author = "Author 4",
                status = BookStatus.READ,
                genreIds = listOf(2)
            )

            val exception = assertThrows<BookNotFoundException> {
                bookRepository.updateBookDetails(nonExistentBookId, bookDetailsUpdatePayload, "en")
            }

            assertTrue(exception.message!!.contains("$nonExistentBookId"))
        }

    @Test
    fun `when updateBookDetails is called with a non-existent genre ID, an ExposedSQLException for foreign key violation should be thrown`() =
        runBlocking {
            val bookDetailsUpdatePayload = BookDetailsUpdatePayload(
                title = "Title 4",
                author = "Author 4",
                status = BookStatus.READ,
                genreIds = listOf(5)
            )

            val exception = assertThrows<ExposedSQLException> {
                bookRepository.updateBookDetails(firstBookId, bookDetailsUpdatePayload, "en")
            }

            assertTrue(exception.message?.contains("violates foreign key constraint") == true)
        }

    @Test
    fun `when updateBookCover is called with a valid book ID and a new path, it should update only the coverPath field of the book`() =
        runBlocking {
            val newCoverPath = "covers/book.jpg"

            val bookBeforeUpdate = bookRepository.getBookById(firstBookId, "en")

            assertNotNull(bookBeforeUpdate)
            assertNull(bookBeforeUpdate.coverPath)

            val updatedBook = bookRepository.updateBookCover(firstBookId, newCoverPath, "en")

            assertEquals(newCoverPath, updatedBook.coverPath)
            val expectedBook = bookBeforeUpdate.copy(coverPath = newCoverPath)
            assertEquals(updatedBook, expectedBook)
            val bookFromDb = bookRepository.getBookById(updatedBook.id, "en")
            assertEquals(expectedBook, bookFromDb)
        }

    @Test
    fun `when updateBookCover is called for a non-existent book ID, a BookNotFoundException should be thrown`() =
        runBlocking {
            val newCoverPath = "covers/book.jpg"

            val exception = assertThrows<BookNotFoundException> {
                bookRepository.updateBookCover(nonExistentBookId, newCoverPath, "en")
            }

            assertTrue(exception.message!!.contains("$nonExistentBookId"))
        }

    @Test
    fun `when deleteBook is called with an existing book ID, it should delete the book and its links from BookGenres table and return true`() =
        runBlocking {
            val wasDeleted = bookRepository.deleteBook(firstBookId)
            assertTrue(wasDeleted)

            val result = bookRepository.getBookById(firstBookId, "en")
            assertNull(result)

            val linksCount = transaction { BookGenres.selectAll().where { BookGenres.bookId eq firstBookId }.count() }
            assertEquals(0, linksCount)
        }

    @Test
    fun `when deleteBook is called with a non-existent book ID, it should do nothing and return false`() = runBlocking {
        val wasDeleted = bookRepository.deleteBook(nonExistentBookId)

        assertFalse(wasDeleted)
    }
}