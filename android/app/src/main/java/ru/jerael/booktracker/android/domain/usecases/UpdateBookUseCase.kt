package ru.jerael.booktracker.android.domain.usecases

import ru.jerael.booktracker.android.domain.model.BookUpdatePayload
import ru.jerael.booktracker.android.domain.repository.BookRepository
import javax.inject.Inject

class UpdateBookUseCase @Inject constructor(
    private val repository: BookRepository
) {
    suspend operator fun invoke(bookUpdatePayload: BookUpdatePayload): Result<Unit> {
        return repository.updateBook(bookUpdatePayload)
    }
}