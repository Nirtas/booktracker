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
