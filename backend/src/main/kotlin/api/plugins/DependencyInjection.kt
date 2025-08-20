package ru.jerael.booktracker.backend.api.plugins

import io.ktor.server.application.*
import org.koin.ktor.plugin.Koin
import ru.jerael.booktracker.backend.api.di.appModule

const val IMAGE_BASE_URL_PROPERTY = "imageBaseUrl"

fun Application.configureDI() {
    install(Koin) {
        properties(mapOf(IMAGE_BASE_URL_PROPERTY to environment.config.property("ktor.storage.baseUrl").getString()))
        modules(appModule(environment))
    }
}