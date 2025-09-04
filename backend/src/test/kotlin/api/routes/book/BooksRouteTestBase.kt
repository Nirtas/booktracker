package api.routes.book

import io.mockk.clearAllMocks
import io.mockk.mockk
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestInstance
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import ru.jerael.booktracker.backend.api.controller.BookController
import ru.jerael.booktracker.backend.api.controller.GenreController
import ru.jerael.booktracker.backend.api.mappers.BookMapperImpl
import ru.jerael.booktracker.backend.api.parsing.MultipartParser
import ru.jerael.booktracker.backend.api.validation.BookValidator
import ru.jerael.booktracker.backend.domain.usecases.book.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class BooksRouteTestBase : KoinTest {

    protected lateinit var getBooksUseCase: GetBooksUseCase
    protected lateinit var addBookUseCase: AddBookUseCase
    protected lateinit var getBookByIdUseCase: GetBookByIdUseCase
    protected lateinit var updateBookDetailsUseCase: UpdateBookDetailsUseCase
    protected lateinit var updateBookCoverUseCase: UpdateBookCoverUseCase
    protected lateinit var deleteBookUseCase: DeleteBookUseCase
    protected lateinit var multipartParser: MultipartParser
    protected lateinit var bookValidator: BookValidator

    protected val imageBaseUrl = ""

    @BeforeAll
    fun setUpKoin() {
        getBooksUseCase = mockk()
        addBookUseCase = mockk()
        getBookByIdUseCase = mockk()
        updateBookDetailsUseCase = mockk()
        updateBookCoverUseCase = mockk()
        deleteBookUseCase = mockk()
        multipartParser = mockk(relaxed = true)
        bookValidator = mockk(relaxed = true)
        startKoin {
            val testModule = module {
                single {
                    BookController(
                        getBooksUseCase,
                        addBookUseCase,
                        getBookByIdUseCase,
                        updateBookDetailsUseCase,
                        updateBookCoverUseCase,
                        deleteBookUseCase,
                        bookValidator,
                        multipartParser,
                        BookMapperImpl(imageBaseUrl)
                    )
                }
                single { mockk<GenreController>() }
            }
            modules(testModule)
        }
    }

    @AfterAll
    fun tearDownKoin() {
        stopKoin()
    }

    @BeforeEach
    fun resetMocks() {
        clearAllMocks()
    }
}