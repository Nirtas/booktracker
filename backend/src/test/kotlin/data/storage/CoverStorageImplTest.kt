package data.storage

import io.ktor.http.content.*
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import ru.jerael.booktracker.backend.data.storage.CoverStorageImpl
import ru.jerael.booktracker.backend.domain.exceptions.ValidationException
import ru.jerael.booktracker.backend.domain.storage.CoverStorage
import ru.jerael.booktracker.backend.domain.storage.FileStorage
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CoverStorageImplTest {

    private val fileStorage: FileStorage = mockk()
    private val coverStorage: CoverStorage = CoverStorageImpl(fileStorage)

    @Test
    fun `when save is called with valid jpg file, it should generate path and call fileStorage`() = runBlocking {
        val filePart: PartData.FileItem = mockk()
        val pathSlot = slot<String>()
        coEvery { filePart.originalFileName } returns "cover.jpg"
        coEvery { filePart.provider() } returns mockk()
        coEvery { fileStorage.saveFile(capture(pathSlot), any()) } returns ""

        coverStorage.save(filePart)

        coVerify(exactly = 1) { fileStorage.saveFile(any(), any()) }
        val capturedPath = pathSlot.captured
        assertTrue(capturedPath.startsWith("covers/"))
        assertTrue(capturedPath.endsWith(".jpg"))
    }

    @Test
    fun `when save is called with blank originalFileName, a ValidationException should be thrown`() = runBlocking {
        val filePart: PartData.FileItem = mockk()
        coEvery { filePart.originalFileName } returns ""

        val exception = assertThrows<ValidationException> {
            coverStorage.save(filePart)
        }

        assertEquals("File name can`t be empty.", exception.message!!)
        coVerify(exactly = 0) { fileStorage.saveFile(any(), any()) }
    }

    @Test
    fun `when save is called with unsupported file extension, a ValidationException should be thrown`() = runBlocking {
        val filePart: PartData.FileItem = mockk()
        coEvery { filePart.originalFileName } returns "cover.gif"

        val exception = assertThrows<ValidationException> {
            coverStorage.save(filePart)
        }

        assertEquals("Invalid file type. Only JPG and PNG are allowed.", exception.message!!)
        coVerify(exactly = 0) { fileStorage.saveFile(any(), any()) }
    }

    @Test
    fun `when fileStorage throws Exception, save should rethrow it without creating a record`() = runBlocking {
        val filePart: PartData.FileItem = mockk()
        coEvery { filePart.originalFileName } returns "cover.jpg"
        coEvery { filePart.provider() } returns mockk()
        coEvery { fileStorage.saveFile(any(), any()) } throws Exception("Error")

        val exception = assertThrows<Exception> {
            coverStorage.save(filePart)
        }
    }
}