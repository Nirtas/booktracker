package ru.jerael.booktracker.backend.api.plugins

import io.ktor.server.application.*
import org.jetbrains.exposed.v1.jdbc.Database

fun Application.configureDatabases() {
    Database.connect(
        url = environment.config.property("ktor.db.url").getString(),
        driver = environment.config.property("ktor.db.driver").getString(),
        user = environment.config.property("ktor.db.user").getString(),
        password = environment.config.property("ktor.db.password").getString()
    )
}