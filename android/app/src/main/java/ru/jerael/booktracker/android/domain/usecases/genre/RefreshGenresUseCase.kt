package ru.jerael.booktracker.android.domain.usecases.genre

import ru.jerael.booktracker.android.domain.repository.GenreRepository
import javax.inject.Inject

class RefreshGenresUseCase @Inject constructor(
    private val repository: GenreRepository
) {
    suspend operator fun invoke(): Result<Unit> {
        return repository.refreshGenres()
    }
}