package ru.jerael.booktracker.backend.domain.storage

import io.ktor.http.content.*

interface CoverStorage {
    suspend fun save(filePart: PartData.FileItem): String
    suspend fun delete(path: String)
}