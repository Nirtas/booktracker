package ru.jerael.booktracker.android.domain.model

import ru.jerael.booktracker.android.domain.mappers.ErrorMapper
import ru.jerael.booktracker.android.domain.model.exceptions.AppException

fun <T> appSuccess(data: T): Result<T> {
    return Result.success(data)
}

fun <T> appFailure(appError: AppError): Result<T> {
    return Result.failure(AppException(appError))
}

suspend fun <T> appFailure(throwable: Throwable, errorMapper: ErrorMapper): Result<T> {
    val appError = errorMapper.map(throwable)
    return Result.failure(AppException(appError))
}