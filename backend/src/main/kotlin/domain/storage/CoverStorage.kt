package ru.jerael.booktracker.backend.domain.storage

interface CoverStorage {
    suspend fun save(content: ByteArray, fileName: String): String
    suspend fun delete(path: String)
}