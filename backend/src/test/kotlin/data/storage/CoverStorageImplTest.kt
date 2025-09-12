package data.storage

import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import ru.jerael.booktracker.backend.data.storage.CoverStorageImpl
import ru.jerael.booktracker.backend.domain.exceptions.ValidationException
import ru.jerael.booktracker.backend.domain.storage.CoverStorage
import ru.jerael.booktracker.backend.domain.storage.FileStorage
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CoverStorageImplTest {

    @MockK
    private lateinit var fileStorage: FileStorage

    private lateinit var coverStorage: CoverStorage

    private val coverBytes: ByteArray = "file content".toByteArray()
    private val coverFileName: String = "cover.jpg"

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        coverStorage = CoverStorageImpl(fileStorage)
    }

    @Test
    fun `when save is called with valid jpg file, it should generate path and call fileStorage`() = runTest {
        val pathSlot = slot<String>()
        coEvery { fileStorage.saveFile(capture(pathSlot), any()) } returns ""

        coverStorage.save(coverBytes, coverFileName)

        coVerify(exactly = 1) { fileStorage.saveFile(any(), any()) }
        val capturedPath = pathSlot.captured
        assertTrue(capturedPath.startsWith("covers/"))
        assertTrue(capturedPath.endsWith(".jpg"))
    }

    @Test
    fun `when save is called with blank originalFileName, a ValidationException should be thrown`() = runTest {
        val exception = assertThrows<ValidationException> {
            coverStorage.save("".toByteArray(), "")
        }

        assertEquals("File name can`t be empty.", exception.message!!)
        coVerify(exactly = 0) { fileStorage.saveFile(any(), any()) }
    }

    @Test
    fun `when save is called with unsupported file extension, a ValidationException should be thrown`() = runTest {
        val exception = assertThrows<ValidationException> {
            coverStorage.save("".toByteArray(), "cover.gif")
        }

        assertEquals("Invalid file type. Only JPG and PNG are allowed.", exception.message!!)
        coVerify(exactly = 0) { fileStorage.saveFile(any(), any()) }
    }

    @Test
    fun `when fileStorage throws Exception, save should rethrow it without creating a record`() = runTest {
        coEvery { fileStorage.saveFile(any(), any()) } throws Exception("Error")

        assertThrows<Exception> {
            coverStorage.save(coverBytes, coverFileName)
        }
    }
}