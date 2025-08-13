package ru.jerael.booktracker.backend.domain.storage

import io.ktor.utils.io.*

interface FileStorage {
    suspend fun saveFile(path: String, channel: ByteReadChannel): String
    suspend fun deleteFile(path: String): Boolean
}