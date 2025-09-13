/*
 * BookTracker is a full-stack application for tracking your reading list.
 * Copyright (C) 2025  Jerael (https://github.com/Nirtas)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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