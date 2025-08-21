package ru.jerael.booktracker.android.domain.util

import ru.jerael.booktracker.android.domain.mappers.ErrorMapper
import ru.jerael.booktracker.android.domain.model.AppError
import ru.jerael.booktracker.android.domain.model.exceptions.AppException

suspend fun Throwable.toAppError(errorMapper: ErrorMapper): AppError {
    return if (this is AppException) {
        this.appError
    } else {
        errorMapper.map(this)
    }
}