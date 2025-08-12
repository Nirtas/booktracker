package ru.jerael.booktracker.android.domain.usecases

import ru.jerael.booktracker.android.domain.model.BookCreationPayload
import ru.jerael.booktracker.android.domain.repository.BookRepository
import javax.inject.Inject

class AddBookUseCase @Inject constructor(
    private val repository: BookRepository
) {
    suspend operator fun invoke(bookCreationPayload: BookCreationPayload): Result<Unit> {
        return repository.addBook(bookCreationPayload)
    }
}