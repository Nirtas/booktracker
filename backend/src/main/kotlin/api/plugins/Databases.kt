package ru.jerael.booktracker.backend.api.plugins

import io.github.cdimascio.dotenv.dotenv
import io.ktor.server.application.*
import org.jetbrains.exposed.v1.jdbc.Database

fun Application.configureDatabases() {
    Database.connect(
        url = "jdbc:postgresql://${dotenv()["DB_HOST"]}:${dotenv()["DB_PORT"]}/${dotenv()["DB_NAME"]}",
        driver = "org.postgresql.Driver",
        user = dotenv()["DB_USER"],
        password = dotenv()["DB_PASSWORD"]
    )
}