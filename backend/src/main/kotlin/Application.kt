package ru.jerael.booktracker.backend

import io.ktor.server.application.*
import ru.jerael.booktracker.backend.api.plugins.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureStatusPages()
    configureDI()
    configureSerialization()
    configureDatabases()
    configureRouting()
}
