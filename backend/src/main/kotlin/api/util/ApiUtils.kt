package ru.jerael.booktracker.backend.api.util

import io.ktor.server.request.*

fun ApplicationRequest.language(): String = this.header("Accept-Language")?.substringBefore(",")?.lowercase() ?: "en"