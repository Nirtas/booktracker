/*
 * BookTracker is a full-stack application for tracking your reading list.
 * Copyright (C) 2025  Jerael (https://github.com/Nirtas)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package data.storage

import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import ru.jerael.booktracker.backend.data.storage.CoverStorageImpl
import ru.jerael.booktracker.backend.domain.storage.CoverStorage
import ru.jerael.booktracker.backend.domain.storage.FileStorage
import kotlin.test.assertEquals

class CoverStorageImplTest {

    @MockK
    private lateinit var fileStorage: FileStorage

    private lateinit var coverStorage: CoverStorage

    private val coverBytes: ByteArray = "file content".toByteArray()
    private val imageBaseUrl = "http://storage.com"
    private val coverPath = "covers/cover.jpg"

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        coverStorage = CoverStorageImpl(fileStorage, imageBaseUrl)
    }

    @Test
    fun `when save is called, it should save the file via fileStorage and return the full URL`() = runTest {
        coEvery { fileStorage.saveFile(any(), any()) } returns ""

        val url = coverStorage.save(coverPath, coverBytes)

        coVerify(exactly = 1) { fileStorage.saveFile(coverPath, any()) }
        val expectedUrl = "$imageBaseUrl/$coverPath"
        assertEquals(expectedUrl, url)
    }

    @Test
    fun `when delete is called, it should call fileStorage with the correct path`() = runTest {
        coEvery { fileStorage.deleteFile(any()) } just Runs

        coverStorage.delete(coverPath)

        coVerify(exactly = 1) { fileStorage.deleteFile(coverPath) }
    }

    @Test
    fun `when fileStorage throws Exception, save should rethrow it without creating a record`() = runTest {
        coEvery { fileStorage.saveFile(any(), any()) } throws Exception("Error")

        assertThrows<Exception> {
            coverStorage.save(coverPath, coverBytes)
        }
    }
}