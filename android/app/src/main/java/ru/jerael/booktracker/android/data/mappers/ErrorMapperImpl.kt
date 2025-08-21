package ru.jerael.booktracker.android.data.mappers

import androidx.sqlite.SQLiteException
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.json.Json
import okio.IOException
import ru.jerael.booktracker.android.data.remote.dto.ErrorDto
import ru.jerael.booktracker.android.domain.mappers.ErrorMapper
import ru.jerael.booktracker.android.domain.model.AppError
import javax.inject.Inject

class ErrorMapperImpl @Inject constructor() : ErrorMapper {
    override suspend fun map(throwable: Throwable): AppError {
        return when (throwable) {
            is IOException -> AppError.NetworkError
            is ClientRequestException, is ServerResponseException -> {
                val errorJson = throwable.response.bodyAsText()
                if (errorJson.isBlank()) {
                    AppError.UnknownError
                } else {
                    try {
                        val errorResponse = Json.decodeFromString<ErrorDto>(errorJson)
                        AppError.ServerError(errorResponse.code)
                    } catch (e: Exception) {
                        AppError.UnknownError
                    }
                }
            }

            is SQLiteException -> AppError.DatabaseError
            else -> AppError.UnknownError
        }
    }
}
