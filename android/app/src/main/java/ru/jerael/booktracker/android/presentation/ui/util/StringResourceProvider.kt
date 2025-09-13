/*
 * BookTracker is a full-stack application for tracking your reading list.
 * Copyright (C) 2025  Jerael (https://github.com/Nirtas)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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