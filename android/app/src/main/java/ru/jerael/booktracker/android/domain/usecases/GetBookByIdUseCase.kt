package ru.jerael.booktracker.android.domain.usecases

import kotlinx.coroutines.flow.Flow
import ru.jerael.booktracker.android.domain.model.Book
import ru.jerael.booktracker.android.domain.repository.BookRepository
import javax.inject.Inject

class GetBookByIdUseCase @Inject constructor(
    private val repository: BookRepository
) {
    operator fun invoke(id: String): Flow<Book?> {
        return repository.getBookById(id)
    }
}