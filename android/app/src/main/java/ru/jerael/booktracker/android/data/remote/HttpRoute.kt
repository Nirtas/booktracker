package ru.jerael.booktracker.android.data.remote

object HttpRoute {
    private const val API_PREFIX = "/api"

    const val BOOKS = "$API_PREFIX/books"

    fun bookById(id: String): String {
        return "$BOOKS/$id"
    }

    fun bookCover(id: String): String {
        return "${bookById(id)}/cover"
    }
}