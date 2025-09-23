package domain.usecases.user

import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import ru.jerael.booktracker.backend.domain.exceptions.UserByIdNotFoundException
import ru.jerael.booktracker.backend.domain.model.user.User
import ru.jerael.booktracker.backend.domain.repository.UserRepository
import ru.jerael.booktracker.backend.domain.usecases.user.GetUserByIdUseCase
import java.util.*

class GetUserByIdUseCaseTest {

    @MockK
    private lateinit var userRepository: UserRepository

    private lateinit var useCase: GetUserByIdUseCase

    private val userId = UUID.fromString("02b3d528-a93f-470a-8fb8-7fb622a086fe")
    private val user = User(
        id = userId,
        email = "test@example.com",
        passwordHash = "hash",
        isVerified = true
    )

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        useCase = GetUserByIdUseCase(userRepository)
    }

    @Test
    fun `when getUserById is called with an existing id, it should return the correct user`() = runTest {
        coEvery { userRepository.getUserById(userId) } returns user

        val result = useCase.invoke(userId)

        assertEquals(user, result)
    }

    @Test
    fun `when getUserById is called with an non-existing id, a UserByIdNotFoundException should be thrown`() = runTest {
        coEvery { userRepository.getUserById(userId) } returns null

        val exception = assertThrows<UserByIdNotFoundException> {
            useCase.invoke(userId)
        }

        assertTrue(exception.message!!.contains("$userId"))
    }
}