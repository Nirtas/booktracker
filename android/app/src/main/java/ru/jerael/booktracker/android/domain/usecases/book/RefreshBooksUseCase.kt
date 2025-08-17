package ru.jerael.booktracker.android.domain.usecases.book

import ru.jerael.booktracker.android.domain.repository.BookRepository
import javax.inject.Inject

class RefreshBooksUseCase @Inject constructor(
    private val repository: BookRepository
) {
    suspend operator fun invoke(): Result<Unit> {
        return repository.refreshBooks()
    }
}