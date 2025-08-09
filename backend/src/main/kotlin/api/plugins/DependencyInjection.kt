package ru.jerael.booktracker.backend.api.plugins

import io.ktor.server.application.*
import org.koin.ktor.plugin.Koin
import ru.jerael.booktracker.backend.api.di.appModule

fun Application.configureDI() {
    install(Koin) {
        modules(appModule)
    }
}