package ru.jerael.booktracker.android.domain.model.book

enum class BookStatus(val value: String) {
    WANT_TO_READ("want_to_read"),
    READING("reading"),
    READ("read");

    companion object {
        fun fromString(value: String): BookStatus? {
            return entries.find { it.value == value.lowercase() }
        }
    }
}

fun BookStatus.toDisplayString(): String {
    return when (this) {
        BookStatus.WANT_TO_READ -> "В планах"
        BookStatus.READING -> "Читаю"
        BookStatus.READ -> "Прочитано"
    }
}