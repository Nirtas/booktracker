package data.repository

import org.jetbrains.exposed.v1.jdbc.Database
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers
abstract class RepositoryTestBase {

    companion object {

        @Container
        val postgresContainer: PostgreSQLContainer<*> =
            PostgreSQLContainer("postgres:17.4").withInitScript("1-schema.sql")

        @JvmStatic
        @BeforeAll
        fun setUp() {
            postgresContainer.start()
            Database.connect(
                url = postgresContainer.jdbcUrl,
                driver = postgresContainer.driverClassName,
                user = postgresContainer.username,
                password = postgresContainer.password
            )
        }

        @JvmStatic
        @AfterAll
        fun tearDown() {
            postgresContainer.stop()
        }
    }
}