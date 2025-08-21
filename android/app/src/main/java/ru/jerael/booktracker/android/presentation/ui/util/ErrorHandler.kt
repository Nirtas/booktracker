package ru.jerael.booktracker.android.presentation.ui.util

import ru.jerael.booktracker.android.domain.mappers.ErrorMapper
import ru.jerael.booktracker.android.domain.util.toAppError
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ErrorHandler @Inject constructor(
    private val stringResourceProvider: StringResourceProvider,
    private val errorMapper: ErrorMapper
) {
    suspend fun handleError(throwable: Throwable): String {
        val appError = throwable.toAppError(errorMapper)
        return stringResourceProvider.getString(appError)
    }
}