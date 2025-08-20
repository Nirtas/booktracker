package ru.jerael.booktracker.backend.data.db.types

import org.jetbrains.exposed.v1.core.Column
import org.jetbrains.exposed.v1.core.Table
import org.postgresql.util.PGobject
import ru.jerael.booktracker.backend.data.db.DbConstants.ENUM_BOOK_STATUS
import ru.jerael.booktracker.backend.domain.exceptions.ValidationException
import ru.jerael.booktracker.backend.domain.model.book.BookStatus

fun Table.bookStatusEnum(name: String): Column<BookStatus> {
    return customEnumeration(
        name = name,
        sql = ENUM_BOOK_STATUS,
        fromDb = { value ->
            BookStatus.fromString(value as String)
                ?: throw ValidationException("Unknown BookStatus value in DB: $value")
        },
        toDb = { enumValue ->
            PGobject().apply {
                type = ENUM_BOOK_STATUS
                value = enumValue.value.uppercase()
            }
        }
    )
}