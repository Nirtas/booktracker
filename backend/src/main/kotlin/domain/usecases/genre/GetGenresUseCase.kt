package ru.jerael.booktracker.backend.domain.usecases.genre

import ru.jerael.booktracker.backend.domain.model.genre.Genre
import ru.jerael.booktracker.backend.domain.repository.GenreRepository

class GetGenresUseCase(private val genreRepository: GenreRepository) {
    suspend operator fun invoke(): List<Genre> {
        return genreRepository.getGenres()
    }
}