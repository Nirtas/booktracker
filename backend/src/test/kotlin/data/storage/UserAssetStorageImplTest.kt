package data.storage

import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import ru.jerael.booktracker.backend.data.storage.UserAssetStorageImpl
import ru.jerael.booktracker.backend.domain.model.AssetType
import ru.jerael.booktracker.backend.domain.storage.FileStorage
import ru.jerael.booktracker.backend.domain.storage.UserAssetStorage
import java.util.*
import kotlin.test.assertEquals

class UserAssetStorageImplTest {

    @MockK
    private lateinit var fileStorage: FileStorage

    private lateinit var userAssetStorage: UserAssetStorage

    private val coverBytes: ByteArray = "file content".toByteArray()
    private val imageBaseUrl = "http://storage.com"
    private val fileName = "cover.jpg"
    private val userId = UUID.randomUUID()
    private val coverPath = "$userId/covers/$fileName"

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        userAssetStorage = UserAssetStorageImpl(fileStorage, imageBaseUrl)
    }

    @Test
    fun `save should generate a correct path and return the full URL`() = runTest {
        val pathSlot = slot<String>()
        coEvery { fileStorage.saveFile(capture(pathSlot), any()) } returns ""

        val resultUrl = userAssetStorage.save(userId, AssetType.COVER, fileName, coverBytes)

        val capturedPath = pathSlot.captured
        assertTrue(capturedPath.startsWith("$userId/covers/"))
        assertTrue(capturedPath.endsWith(".jpg"))
        assertEquals("$imageBaseUrl/$capturedPath", resultUrl)
    }

    @Test
    fun `when delete is called, it should call fileStorage with the correct path`() = runTest {
        coEvery { fileStorage.deleteFile(any()) } just Runs

        userAssetStorage.delete(coverPath)

        coVerify(exactly = 1) { fileStorage.deleteFile(coverPath) }
    }

    @Test
    fun `when fileStorage throws Exception, save should rethrow it without creating a record`() = runTest {
        coEvery { fileStorage.saveFile(any(), any()) } throws Exception("Error")

        assertThrows<Exception> {
            userAssetStorage.save(userId, AssetType.COVER, fileName, coverBytes)
        }
    }

    @Test
    fun `when deleteUserDirectory is called, it should invoke deleteDirectory with the correct user id path`() =
        runTest {
            coEvery { fileStorage.deleteDirectory(any()) } just Runs

            userAssetStorage.deleteUserDirectory(userId)

            coVerify(exactly = 1) { fileStorage.deleteDirectory(userId.toString()) }
        }
}