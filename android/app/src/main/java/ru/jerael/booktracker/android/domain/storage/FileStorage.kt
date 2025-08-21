package ru.jerael.booktracker.android.domain.storage

import android.net.Uri
import java.io.File

interface FileStorage {
    suspend fun saveFile(uri: Uri): Result<File>
}