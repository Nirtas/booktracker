package api.routes.genre

import io.mockk.MockKAnnotations
import io.mockk.clearAllMocks
import io.mockk.impl.annotations.MockK
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
import ru.jerael.booktracker.backend.api.mappers.GenreMapperImpl
import ru.jerael.booktracker.backend.domain.usecases.genre.GetGenresUseCase

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class GenresRouteTestBase : KoinTest {

    @MockK
    protected lateinit var getGenresUseCase: GetGenresUseCase

    @BeforeAll
    fun setUpKoin() {
        MockKAnnotations.init(this)
        startKoin {
            val testModule = module {
                single { GenreController(getGenresUseCase, GenreMapperImpl()) }
                single { mockk<BookController>() }
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