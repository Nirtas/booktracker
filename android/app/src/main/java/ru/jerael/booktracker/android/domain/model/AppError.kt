package ru.jerael.booktracker.android.domain.model

sealed interface AppError {
    object NetworkError : AppError
    data class ServerError(val code: String) : AppError
    object UnknownError : AppError
    object DatabaseError : AppError
    object FileStorageError : AppError
    object NotFoundError : AppError
}