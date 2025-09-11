package ru.jerael.booktracker.android.data.local.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import app.cash.turbine.test
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull
import org.junit.jupiter.api.assertNull
import ru.jerael.booktracker.android.data.local.BookDatabase
import ru.jerael.booktracker.android.data.local.entity.BookEntity
import ru.jerael.booktracker.android.data.local.entity.BookGenresEntity
import ru.jerael.booktracker.android.data.local.entity.GenreEntity
import ru.jerael.booktracker.android.data.local.relations.BookWithGenres
import ru.jerael.booktracker.android.domain.model.book.BookStatus
import java.time.Instant
import java.util.UUID

class BookDaoTest {

    private lateinit var db: BookDatabase
    private lateinit var bookDao: BookDao
    private lateinit var genreDao: GenreDao

    private val firstGenre = GenreEntity(id = 1, name = "gaming")
    private val secondGenre = GenreEntity(id = 2, name = "adventure")
    private val thirdGenre = GenreEntity(id = 3, name = "science fiction")


    private val firstBookId = "e607ac1d-309f-4301-b71f-52b59e7cb4db"
    private val firstBook = BookEntity(
        id = firstBookId,
        title = "Title 1",
        author = "Author 1",
        coverUrl = null,
        status = BookStatus.READ.value,
        createdAt = Instant.now().toEpochMilli()
    )

    private val secondBookId = "8ec9c02f-3604-4b1a-8fdb-165b8fd6339b"
    private val secondBook = BookEntity(
        id = secondBookId,
        title = "Title 2",
        author = "Author 2",
        coverUrl = null,
        status = BookStatus.READ.value,
        createdAt = Instant.now().toEpochMilli()
    )

    private val thirdBookId = "06d78bfc-0341-4c93-9bcb-0a0598da4789"
    private val thirdBook = BookEntity(
        id = thirdBookId,
        title = "Title 3",
        author = "Author 3",
        coverUrl = null,
        status = BookStatus.READ.value,
        createdAt = Instant.now().toEpochMilli()
    )

    private val nonExistentBookId = "d9773a14-d1f4-48e0-ad75-2a1635e9a857"

    private val firstBookGenres = listOf(
        BookGenresEntity(firstBookId, firstGenre.id),
        BookGenresEntity(firstBookId, secondGenre.id)
    )

    private val secondBookGenres = listOf(BookGenresEntity(secondBookId, thirdGenre.id))

    private val genres = listOf(firstGenre, secondGenre, thirdGenre)

    private suspend fun initGenreTableWithTestData() {
        genreDao.upsertAll(genres)
    }

    private suspend fun initBookWithGenresTableWithTestData() {
        initGenreTableWithTestData()
        bookDao.upsertBookWithGenres(firstBook, firstBookGenres)
        bookDao.upsertBookWithGenres(secondBook, secondBookGenres)
        bookDao.upsertBookWithGenres(thirdBook, emptyList())
    }

    @BeforeEach
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, BookDatabase::class.java)
            .allowMainThreadQueries().build()
        bookDao = db.bookDao()
        genreDao = db.genreDao()
    }

    @AfterEach
    fun closeDb() {
        db.close()
    }

    @Test
    fun whenDatabaseIsEmpty_getBooksWithGenresReturnsFlowEmittingAnEmptyList() = runTest {
        bookDao.getBooksWithGenres().test {
            val list = awaitItem()
            assertTrue(list.isEmpty())
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun whenDatabaseContainsBooksAndGenres_getBooksWithGenresReturnsFlowEmittingListOfBooksWithTheirGenres() =
        runTest {
            initBookWithGenresTableWithTestData()

            bookDao.getBooksWithGenres().test {
                val list = awaitItem()
                assertEquals(3, list.size)

                val firstBook = list.find { it.book.id == firstBookId }
                assertNotNull(firstBook)
                assertEquals(setOf(firstGenre, secondGenre), firstBook.genres.toSet())

                val secondBook = list.find { it.book.id == secondBookId }
                assertNotNull(secondBook)
                assertEquals(setOf(thirdGenre), secondBook.genres.toSet())

                val thirdBook = list.find { it.book.id == thirdBookId }
                assertNotNull(thirdBook)
                assertTrue(thirdBook.genres.isEmpty())

                cancelAndConsumeRemainingEvents()
            }
        }

    @Test
    fun whenValidBookIdIsProvided_getBookWithGenresByIdReturnsFlowEmittingTheCorrectBookWithItsGenres() =
        runTest {
            initBookWithGenresTableWithTestData()

            bookDao.getBookWithGenresById(firstBookId).test {
                val book = awaitItem()
                assertNotNull(book)
                assertEquals(setOf(firstGenre, secondGenre), book.genres.toSet())
                cancelAndConsumeRemainingEvents()
            }
        }

    @Test
    fun whenBookIdNotExist_getBookWithGenresByIdReturnsFlowEmittingNull() = runTest {
        initBookWithGenresTableWithTestData()

        bookDao.getBookWithGenresById(nonExistentBookId).test {
            val book = awaitItem()
            assertNull(book)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun whenBookGenresTableContainsData_getBookGenresReturnsIt() = runTest {
        initBookWithGenresTableWithTestData()

        val list = bookDao.getBookGenres()

        assertEquals((firstBookGenres + secondBookGenres).toSet(), list.toSet())
    }

    @Test
    fun whenBookGenresTableIsEmpty_getBookGenresReturnsAnEmptyList() = runTest {
        val list = bookDao.getBookGenres()

        assertTrue(list.isEmpty())
    }

    @Test
    fun whenNewBookEntityIsProvided_upsertBookInsertsItIntoTheDatabase() = runTest {
        bookDao.upsertBook(firstBook)

        val expectedBook = BookWithGenres(firstBook, emptyList())

        bookDao.getBookWithGenresById(firstBookId).test {
            val book = awaitItem()
            assertNotNull(book)
            assertEquals(expectedBook, book)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun whenAnExistingBookEntityIsProvidedWithUpdatedData_upsertBookUpdatesItsDetails() = runTest {
        initBookWithGenresTableWithTestData()

        val newBook = firstBook.copy(title = "Title new")
        bookDao.upsertBook(newBook)

        val expectedBook = BookWithGenres(newBook, listOf(firstGenre, secondGenre))

        bookDao.getBookWithGenresById(newBook.id).test {
            val book = awaitItem()
            assertNotNull(book)
            assertEquals(expectedBook, book)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun whenListOfNewBookGenresRelationsIsProvided_upsertBookGenresInsertsAllOfThem() = runTest {
        initBookWithGenresTableWithTestData()

        val newFirstBookGenres = listOf(BookGenresEntity(firstBookId, thirdGenre.id))
        bookDao.upsertBookGenres(newFirstBookGenres)

        val expectedGenres = setOf(firstGenre, secondGenre, thirdGenre)

        bookDao.getBookWithGenresById(firstBookId).test {
            val book = awaitItem()
            assertNotNull(book)
            assertEquals(expectedGenres, book.genres.toSet())
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun whenEmptyListIsProvided_upsertBookGenresExecutesWithoutErrorAndMakesNoChanges() = runTest {
        initBookWithGenresTableWithTestData()
        val expectedBookGenres = bookDao.getBookGenres()

        bookDao.upsertBookGenres(emptyList())

        val actualBookGenres = bookDao.getBookGenres()
        assertEquals(expectedBookGenres, actualBookGenres)
    }

    @Test
    fun whenNewBookAndItsGenresAreProvided_upsertBookWithGenresInsertsBookAndAllItsGenreRelations() =
        runTest {
            initGenreTableWithTestData()

            bookDao.upsertBookWithGenres(firstBook, firstBookGenres)

            val expectedGenres = setOf(firstGenre, secondGenre)

            bookDao.getBooksWithGenres().test {
                val list = awaitItem()
                assertEquals(1, list.size)
                assertEquals(firstBook, list.first().book)
                assertEquals(expectedGenres, list.first().genres.toSet())
            }
        }

    @Test
    fun whenExistingBookGenresAreChanged_upsertBookWithGenresReplacesAllPreviousGenreAssociationsWithTheNewOnes() =
        runTest {
            initBookWithGenresTableWithTestData()

            val newGenres = listOf(BookGenresEntity(firstBookId, thirdGenre.id))
            bookDao.upsertBookWithGenres(firstBook, newGenres)

            val expectedGenres = setOf(thirdGenre)

            bookDao.getBookWithGenresById(firstBookId).test {
                val book = awaitItem()
                assertNotNull(book)
                assertEquals(expectedGenres, book.genres.toSet())
                cancelAndConsumeRemainingEvents()
            }
        }

    @Test
    fun whenExistingBookDetailsAreUpdated_upsertBookWithGenresUpdatesTheBookFieldsWhilePreservingItsGenres() =
        runTest {
            initBookWithGenresTableWithTestData()

            val newBook = firstBook.copy(title = "Title new")
            bookDao.upsertBookWithGenres(newBook, firstBookGenres)

            val expectedBook = BookWithGenres(newBook, listOf(firstGenre, secondGenre))

            bookDao.getBookWithGenresById(firstBookId).test {
                val book = awaitItem()
                assertNotNull(book)
                assertEquals(expectedBook, book)
                cancelAndConsumeRemainingEvents()
            }
        }

    @Test
    fun whenExistingBookIsUpdatedWithEmptyGenreList_upsertBookWithGenresRemovesAllItsGenres() =
        runTest {
            initBookWithGenresTableWithTestData()

            bookDao.upsertBookWithGenres(firstBook, emptyList())

            bookDao.getBookWithGenresById(firstBookId).test {
                val book = awaitItem()
                assertNotNull(book)
                assertTrue(book.genres.isEmpty())
                cancelAndConsumeRemainingEvents()
            }
        }

    @Test
    fun whenBooksTableContainsData_clearBooksRemovesAllEntriesFromItAndAllCorrespondingEntriesInTheBookGenresTable() =
        runTest {
            initBookWithGenresTableWithTestData()

            bookDao.clearBooks()

            val books = bookDao.getBooks()
            assertTrue(books.isEmpty())

            val bookGenres = bookDao.getBookGenres()
            assertTrue(bookGenres.isEmpty())

            genreDao.getAll().test {
                val list = awaitItem()
                assertEquals(genres.sortedBy { it.name }, list)
                cancelAndConsumeRemainingEvents()
            }
        }

    @Test
    fun whenDatabaseContainsData_clearAndInsertBooksReplacesAllExistingBooksAndBookGenresWithTheNewProvidedData() =
        runTest {
            initBookWithGenresTableWithTestData()

            val newBookId = UUID.randomUUID().toString()
            val newBook = BookEntity(
                id = newBookId,
                title = "Title new",
                author = "Author new",
                coverUrl = null,
                status = BookStatus.READ.value,
                createdAt = Instant.now().toEpochMilli()
            )
            val newBookGenres = listOf(
                BookGenresEntity(newBookId, firstGenre.id),
                BookGenresEntity(newBookId, secondGenre.id),
                BookGenresEntity(newBookId, thirdGenre.id),
            )

            bookDao.clearAndInsertBooks(listOf(newBook), newBookGenres)

            val expectedBooksWithGenres = listOf(BookWithGenres(newBook, genres))

            bookDao.getBooksWithGenres().test {
                val list = awaitItem()
                assertEquals(1, list.size)
                assertEquals(
                    expectedBooksWithGenres.first().genres.toSet(),
                    list.first().genres.toSet()
                )
                cancelAndConsumeRemainingEvents()
            }
        }

    @Test
    fun whenEmptyListsAreProvided_clearAndInsertBooksClearsAllBookAndBookGenresTables() = runTest {
        initBookWithGenresTableWithTestData()

        bookDao.clearAndInsertBooks(emptyList(), emptyList())

        bookDao.getBooksWithGenres().test {
            val list = awaitItem()
            assertTrue(list.isEmpty())
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun whenValidBookIdIsProvided_deleteBookGenresRemovesAllGenreAssociationsForThatBookButLeavesBookItself() =
        runTest {
            initBookWithGenresTableWithTestData()

            bookDao.deleteBookGenres(firstBookId)

            bookDao.getBookWithGenresById(firstBookId).test {
                val book = awaitItem()
                assertNotNull(book)
                assertTrue(book.genres.isEmpty())
                cancelAndConsumeRemainingEvents()
            }
        }

    @Test
    fun whenBookIsDeleted_itsCorrespondingEntriesInBookGenresTableShouldAlsoBeRemoved() = runTest {
        initBookWithGenresTableWithTestData()

        bookDao.deleteBookById(firstBookId)

        bookDao.getBookWithGenresById(firstBookId).test {
            val book = awaitItem()
            assertNull(book)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun whenValidBookIdIsProvided_deleteBookByIdRemovesOnlyThatBookFromTheDatabase() = runTest {
        initBookWithGenresTableWithTestData()

        bookDao.deleteBookById(firstBookId)

        bookDao.getBookWithGenresById(firstBookId).test {
            val book = awaitItem()
            assertNull(book)
            cancelAndConsumeRemainingEvents()
        }

        bookDao.getBooksWithGenres().test {
            val list = awaitItem()
            assertEquals(2, list.size)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun whenNonExistentBookIdIsProvided_deleteBookByIdExecutesWithoutErrorAndNotAffectOtherData() =
        runTest {
            initBookWithGenresTableWithTestData()

            bookDao.deleteBookById(nonExistentBookId)

            val bookCount = bookDao.getBooksWithGenres().first().size
            assertEquals(3, bookCount)
        }
}