package ru.jerael.booktracker.backend.api.util

import io.ktor.server.request.*

fun ApplicationRequest.language(): String {
    val languageTag = this.header("Accept-Language")?.substringBefore(",")
    return languageTag?.substringBefore("-")?.lowercase() ?: "en"
}