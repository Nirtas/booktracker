package data.storage

import io.ktor.http.content.*
import io.mockk.*
import io.mockk.impl.annotations.MockK
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

    @MockK
    private lateinit var filePart: PartData.FileItem

    private lateinit var coverStorage: CoverStorage

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        coverStorage = CoverStorageImpl(fileStorage)
    }

    @Test
    fun `when save is called with valid jpg file, it should generate path and call fileStorage`() = runTest {
        val pathSlot = slot<String>()
        every { filePart.originalFileName } returns "cover.jpg"
        every { filePart.provider() } returns mockk()
        coEvery { fileStorage.saveFile(capture(pathSlot), any()) } returns ""

        coverStorage.save(filePart)

        coVerify(exactly = 1) { fileStorage.saveFile(any(), any()) }
        val capturedPath = pathSlot.captured
        assertTrue(capturedPath.startsWith("covers/"))
        assertTrue(capturedPath.endsWith(".jpg"))
    }

    @Test
    fun `when save is called with blank originalFileName, a ValidationException should be thrown`() = runTest {
        every { filePart.originalFileName } returns ""

        val exception = assertThrows<ValidationException> {
            coverStorage.save(filePart)
        }

        assertEquals("File name can`t be empty.", exception.message!!)
        coVerify(exactly = 0) { fileStorage.saveFile(any(), any()) }
    }

    @Test
    fun `when save is called with unsupported file extension, a ValidationException should be thrown`() = runTest {
        every { filePart.originalFileName } returns "cover.gif"

        val exception = assertThrows<ValidationException> {
            coverStorage.save(filePart)
        }

        assertEquals("Invalid file type. Only JPG and PNG are allowed.", exception.message!!)
        coVerify(exactly = 0) { fileStorage.saveFile(any(), any()) }
    }

    @Test
    fun `when fileStorage throws Exception, save should rethrow it without creating a record`() = runTest {
        every { filePart.originalFileName } returns "cover.jpg"
        every { filePart.provider() } returns mockk()
        coEvery { fileStorage.saveFile(any(), any()) } throws Exception("Error")

        val exception = assertThrows<Exception> {
            coverStorage.save(filePart)
        }
    }
}