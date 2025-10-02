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

import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.io.TempDir
import ru.jerael.booktracker.backend.data.storage.FileStorageImpl
import ru.jerael.booktracker.backend.domain.exceptions.StorageException
import ru.jerael.booktracker.backend.domain.storage.FileStorage
import java.io.ByteArrayInputStream
import java.io.File

class FileStorageImplTest {

    private lateinit var fileStorage: FileStorage

    @TempDir
    lateinit var tempDir: File

    @BeforeEach
    fun setUp() {
        fileStorage = FileStorageImpl(
            storagePath = tempDir.absolutePath,
            logger = mockk(relaxed = true)
        )
    }

    @Test
    fun `when saveFile is called with valid data, it should create file and write content to it`() = runTest {
        val path = "test/file.txt"
        val content = "file content"
        val inputStream = ByteArrayInputStream(content.toByteArray())

        val result = fileStorage.saveFile(path, inputStream)

        assertEquals(path, result)
        val file = File(tempDir, path)
        assertTrue(file.exists())
        assertEquals(content, file.readText())
    }

    @Test
    fun `when saveFile is called with an empty InputStream, a StorageException should be thrown`() = runTest {
        val path = "test/file.txt"
        val content = ""
        val inputStream = ByteArrayInputStream(content.toByteArray())

        assertThrows<StorageException> {
            fileStorage.saveFile(path, inputStream)
        }

        val file = File(tempDir, path)
        assertFalse(file.exists())
    }

    @Test
    fun `when saveFile is called and disk write fails, a StorageException should be thrown`() = runTest {
        val parentPath = "test"
        val fullPath = "$parentPath/file.txt"
        val inputStream = ByteArrayInputStream("file content".toByteArray())

        val parentAsFile = File(tempDir, parentPath)
        parentAsFile.writeText("")
        assertTrue(parentAsFile.isFile)

        assertThrows<StorageException> {
            fileStorage.saveFile(fullPath, inputStream)
        }

        val attemptedFile = File(tempDir, fullPath)
        assertFalse(attemptedFile.exists())
    }

    @Test
    fun `when deleteFile is called with an existing file path, it should remove the file from disk`() = runTest {
        val path = "file.txt"
        val file = File(tempDir, path)
        file.writeText("file content")

        fileStorage.deleteFile(path)

        assertFalse(file.exists())
    }

    @Test
    fun `when deleteFile is called with an non-existing file path, it should complete without errors`() = runTest {
        val path = "file.txt"

        fileStorage.deleteFile(path)
    }

    @Test
    fun `when deleteFile is called on a non-empty directory, a StorageException should be thrown`() = runTest {
        val dirPath = "test"
        val directory = File(tempDir, dirPath)
        directory.mkdir()

        val file = File(directory, "file.txt")
        file.writeText("")

        assertThrows<StorageException> {
            fileStorage.deleteFile(dirPath)
        }

        assertTrue(directory.exists())
        assertTrue(file.exists())
    }

    @Test
    fun `when deleteDirectory is called with an existing directory, it should remove the directory and its content`() =
        runTest {
            val dirPath = "test"
            val directory = File(tempDir, dirPath)
            directory.mkdir()

            val file = File(directory, "file.txt")
            file.writeText("")

            fileStorage.deleteDirectory(dirPath)

            assertFalse(directory.exists())
            assertFalse(file.exists())
        }

    @Test
    fun `when deleteDirectory is called with a non-existing path, it should complete without errors`() = runTest {
        val path = "non-existing-dir"

        fileStorage.deleteDirectory(path)
    }

    @Test
    fun `when deleteDirectory is called on a file path, it should do nothing and complete without errors`() = runTest {
        val path = "file.txt"
        val file = File(tempDir, path)
        file.writeText("content")

        fileStorage.deleteDirectory(path)

        assertTrue(file.exists())
    }
}