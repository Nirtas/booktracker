package api.routes.book

import io.mockk.MockKAnnotations
import io.mockk.clearAllMocks
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
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
import ru.jerael.booktracker.backend.api.mappers.GenreMapperImpl
import ru.jerael.booktracker.backend.api.parsing.MultipartParser
import ru.jerael.booktracker.backend.api.validation.BookValidator
import ru.jerael.booktracker.backend.domain.usecases.book.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class BooksRouteTestBase : KoinTest {

    @MockK
    protected lateinit var getBooksUseCase: GetBooksUseCase

    @MockK
    protected lateinit var addBookUseCase: AddBookUseCase

    @MockK
    protected lateinit var getBookByIdUseCase: GetBookByIdUseCase

    @MockK
    protected lateinit var updateBookDetailsUseCase: UpdateBookDetailsUseCase

    @MockK
    protected lateinit var updateBookCoverUseCase: UpdateBookCoverUseCase

    @MockK
    protected lateinit var deleteBookUseCase: DeleteBookUseCase

    @RelaxedMockK
    protected lateinit var multipartParser: MultipartParser

    @RelaxedMockK
    protected lateinit var bookValidator: BookValidator

    protected val imageBaseUrl = ""

    @BeforeAll
    fun setUpKoin() {
        MockKAnnotations.init(this)
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
                        BookMapperImpl(imageBaseUrl, GenreMapperImpl())
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