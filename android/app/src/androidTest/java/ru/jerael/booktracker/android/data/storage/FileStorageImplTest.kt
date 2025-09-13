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

package ru.jerael.booktracker.android.data.storage

import android.app.Application
import android.net.Uri
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull
import ru.jerael.booktracker.android.domain.model.AppError
import ru.jerael.booktracker.android.domain.model.exceptions.AppException
import ru.jerael.booktracker.android.domain.storage.FileStorage
import java.io.File

class FileStorageImplTest {

    private lateinit var application: Application
    private lateinit var fileStorage: FileStorage

    private lateinit var file: File
    private var createdFile: File? = null

    @BeforeEach
    fun setUp() {
        application = ApplicationProvider.getApplicationContext()
        fileStorage = FileStorageImpl(application)
        file = File.createTempFile("file", ".txt").apply {
            writeText("file content")
        }
    }

    @AfterEach
    fun tearDown() {
        if (file.exists()) {
            file.delete()
        }
        createdFile?.let {
            if (it.exists()) {
                it.delete()
            }
        }
    }

    @Test
    fun whenSaveFileIsCalledWithValidUri_itShouldCreateFileAndWriteContentToIt() = runTest {
        val uri = Uri.fromFile(file)

        val result = fileStorage.saveFile(uri)

        assertTrue(result.isSuccess)
        createdFile = result.getOrNull()
        assertNotNull(createdFile)
        assertTrue(createdFile!!.exists())
        assertTrue(createdFile!!.name.startsWith("cover_"))
        assertEquals("file content", createdFile!!.readText())
    }

    @Test
    fun whenSaveFileIsCalledWithUriOfNonExistentFile_itShouldReturnFileStorageError() = runTest {
        val nonExistentFile = File("test", "non_existent_file.txt")
        val uri = Uri.fromFile(nonExistentFile)

        val result = fileStorage.saveFile(uri)

        assertTrue(result.isFailure)
        val exception = result.exceptionOrNull() as? AppException
        assertNotNull(exception)
        assertEquals(AppError.FileStorageError, exception.appError)
    }
}