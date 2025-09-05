package ru.jerael.booktracker.backend.domain.usecases

import ru.jerael.booktracker.backend.domain.exceptions.ValidationException
import ru.jerael.booktracker.backend.domain.repository.GenreRepository

class GenresValidator(private val genreRepository: GenreRepository) {
    suspend operator fun invoke(genreIds: List<Int>, language: String) {
        val uniqueGenres = genreIds.distinct()
        if (uniqueGenres.isEmpty()) {
            return
        }
        val foundGenres = genreRepository.getGenresByIds(uniqueGenres, language)
        if (foundGenres.size != uniqueGenres.size) {
            val notFoundGenreIds = uniqueGenres.toSet() - foundGenres.map { it.id }.toSet()
            throw ValidationException("One or more genres not found: ${notFoundGenreIds.joinToString()}")
        }
    }
}