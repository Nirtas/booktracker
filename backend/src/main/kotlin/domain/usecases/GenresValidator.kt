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

package ru.jerael.booktracker.backend.domain.usecases

import ru.jerael.booktracker.backend.api.validation.ValidationError
import ru.jerael.booktracker.backend.api.validation.ValidationException
import ru.jerael.booktracker.backend.api.validation.codes.GenreValidationErrorCode
import ru.jerael.booktracker.backend.domain.repository.GenreRepository

class GenresValidator(private val genreRepository: GenreRepository) {
    suspend operator fun invoke(genreIds: List<Int>, language: String) {
        val uniqueGenres = genreIds.distinct()
        if (uniqueGenres.isEmpty()) {
            return
        }
        val foundGenres = genreRepository.getGenresByIds(uniqueGenres, language)
        if (foundGenres.size != uniqueGenres.size) {
            val notFoundGenreIds = (uniqueGenres.toSet() - foundGenres.map { it.id }.toSet()).map { it.toString() }
            val error = ValidationError(
                code = GenreValidationErrorCode.NOT_FOUND,
                params = mapOf("notFound" to notFoundGenreIds)
            )
            throw ValidationException(mapOf("genres" to listOf(error)))
        }
    }
}