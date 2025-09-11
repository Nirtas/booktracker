package ru.jerael.booktracker.android.domain.usecases.book

import android.net.Uri
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import ru.jerael.booktracker.android.domain.model.appSuccess
import ru.jerael.booktracker.android.domain.storage.FileStorage
import java.io.File
import java.io.IOException

class SaveCoverFileUseCaseTest {

    @MockK
    private lateinit var fileStorage: FileStorage

    private lateinit var useCase: SaveCoverFileUseCase

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        useCase = SaveCoverFileUseCase(fileStorage)
    }

    @Test
    fun `when coverUri is present and storage succeeds, should return success with file`() =
        runTest {
            val coverFile = mockk<File>()
            val coverUri = mockk<Uri>()
            coEvery { fileStorage.saveFile(coverUri) } returns appSuccess(coverFile)

            val result = useCase(coverUri)

            assertTrue(result.isSuccess)
            assertEquals(coverFile, result.getOrNull())
            coVerify(exactly = 1) { fileStorage.saveFile(coverUri) }
        }

    @Test
    fun `when coverUri is null, should return success with null`() = runTest {
        val coverUri: Uri? = null

        val result = useCase(coverUri)

        assertTrue(result.isSuccess)
        assertNull(result.getOrNull())
        coVerify(exactly = 0) { fileStorage.saveFile(any()) }
    }

    @Test
    fun `when coverUri is present and storage fails, should propagate the failure`() = runTest {
        val coverUri = mockk<Uri>()
        val storageException = IOException("Error")
        coEvery { fileStorage.saveFile(coverUri) } returns Result.failure(storageException)

        val result = useCase(coverUri)

        assertTrue(result.isFailure)
        assertEquals(storageException, result.exceptionOrNull())
        coVerify(exactly = 1) { fileStorage.saveFile(coverUri) }
    }
}