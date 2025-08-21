package ru.jerael.booktracker.android.presentation.ui.util

import android.annotation.SuppressLint
import android.content.Context
import androidx.annotation.StringRes
import dagger.hilt.android.qualifiers.ApplicationContext
import ru.jerael.booktracker.android.R
import ru.jerael.booktracker.android.domain.model.AppError
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StringResourceProvider @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun getString(@StringRes resId: Int): String {
        return context.getString(resId)
    }

    @SuppressLint("DiscouragedApi")
    fun getString(appError: AppError): String {
        return when (appError) {
            is AppError.NetworkError -> context.getString(R.string.error_network)
            is AppError.UnknownError -> context.getString(R.string.error_unknown)
            is AppError.ServerError -> {
                val resourceId = context.resources.getIdentifier(
                    "error_code_${appError.code}",
                    "string",
                    context.packageName
                )
                if (resourceId != 0) {
                    context.getString(resourceId)
                } else {
                    context.getString(R.string.error_unknown)
                }
            }

            is AppError.DatabaseError -> context.getString(R.string.error_database)
            is AppError.FileStorageError -> context.getString(R.string.error_file_storage)
            is AppError.NotFoundError -> context.getString(R.string.error_not_found)
        }
    }
}