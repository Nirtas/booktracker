package ru.jerael.booktracker.android.domain.usecases

import ru.jerael.booktracker.android.domain.repository.BookRepository
import javax.inject.Inject

class DeleteBookUseCase @Inject constructor(
    private val repository: BookRepository
) {
    suspend operator fun invoke(id: String): Result<Unit> {
        return repository.deleteBook(id)
    }
}