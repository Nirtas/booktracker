package ru.jerael.booktracker.backend

import io.ktor.server.application.*
import ru.jerael.booktracker.backend.api.plugins.configureDI
import ru.jerael.booktracker.backend.api.plugins.configureRouting
import ru.jerael.booktracker.backend.api.plugins.configureSerialization

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureDI()
    configureSerialization()
    configureRouting()
}
