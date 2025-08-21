package ru.jerael.booktracker.android.domain.usecases.genre

import kotlinx.coroutines.flow.Flow
import ru.jerael.booktracker.android.domain.model.genre.Genre
import ru.jerael.booktracker.android.domain.repository.GenreRepository
import javax.inject.Inject

class GetGenresUseCase @Inject constructor(
    private val repository: GenreRepository
) {
    operator fun invoke(): Flow<Result<List<Genre>>> {
        return repository.getGenres()
    }
}