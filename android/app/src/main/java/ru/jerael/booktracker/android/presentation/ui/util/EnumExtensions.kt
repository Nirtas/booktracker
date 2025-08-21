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