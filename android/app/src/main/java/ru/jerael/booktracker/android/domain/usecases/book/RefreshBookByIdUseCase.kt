package ru.jerael.booktracker.android.domain.usecases.book

import ru.jerael.booktracker.android.domain.repository.BookRepository
import javax.inject.Inject

class RefreshBookByIdUseCase @Inject constructor(
    private val repository: BookRepository
) {
    suspend operator fun invoke(id: String): Result<Unit> {
        return repository.refreshBookById(id)
    }
}