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

package ru.jerael.booktracker.android.presentation.ui.screens.book_list

import ru.jerael.booktracker.android.domain.model.book.Book
import ru.jerael.booktracker.android.domain.model.genre.Genre

data class BookListUiState(
    val books: List<Book> = emptyList(),
    val userMessage: String? = null,
    val isInitialLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val isFilterSheetVisible: Boolean = false,
    val activeFilters: BookListFilterState = BookListFilterState(),
    val allGenres: List<Genre> = emptyList(),
    val searchQuery: String = ""
)