package ru.jerael.booktracker.android.domain.mappers

import ru.jerael.booktracker.android.domain.model.AppError

interface ErrorMapper {
    suspend fun map(throwable: Throwable): AppError
}