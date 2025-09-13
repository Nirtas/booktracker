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

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import ru.jerael.booktracker.android.R
import ru.jerael.booktracker.android.domain.model.book.BookStatus
import ru.jerael.booktracker.android.presentation.ui.model.SortBy
import ru.jerael.booktracker.android.presentation.ui.model.SortOrder

@Composable
fun SortBy.toDisplayString(): String {
    return when (this) {
        SortBy.TITLE -> stringResource(R.string.sort_by_title)
        SortBy.AUTHOR -> stringResource(R.string.sort_by_author)
        SortBy.DATE_ADDED -> stringResource(R.string.sort_by_date_added)
    }
}

@Composable
fun SortOrder.toDisplayString(): String {
    return when (this) {
        SortOrder.ASCENDING -> stringResource(R.string.sort_order_ascending)
        SortOrder.DESCENDING -> stringResource(R.string.sort_order_descending)
    }
}

@Composable
fun BookStatus.toDisplayString(): String {
    return when (this) {
        BookStatus.WANT_TO_READ -> stringResource(R.string.want_to_read)
        BookStatus.READING -> stringResource(R.string.reading)
        BookStatus.READ -> stringResource(R.string.read)
    }
}