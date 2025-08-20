package ru.jerael.booktracker.android.presentation.ui.screens.book_list

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.jerael.booktracker.android.domain.model.book.Book
import ru.jerael.booktracker.android.domain.model.genre.Genre
import ru.jerael.booktracker.android.domain.usecases.book.GetBooksUseCase
import ru.jerael.booktracker.android.domain.usecases.book.RefreshBooksUseCase
import ru.jerael.booktracker.android.domain.usecases.genre.GetGenresUseCase
import ru.jerael.booktracker.android.presentation.ui.model.SortBy
import ru.jerael.booktracker.android.presentation.ui.model.SortOrder
import javax.inject.Inject

@HiltViewModel
class BookListViewModel @Inject constructor(
    getBooksUseCase: GetBooksUseCase,
    private val refreshBooksUseCase: RefreshBooksUseCase,
    private val getGenresUseCase: GetGenresUseCase
) : ViewModel() {

    private val _books: Flow<List<Book>> = getBooksUseCase()
    private val _userMessage: MutableStateFlow<String?> = MutableStateFlow(null)
    private val _isInitialLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    private val _isRefreshing: MutableStateFlow<Boolean> = MutableStateFlow(false)

    private data class FilterState(
        val isFilterSheetVisible: Boolean = false,
        val activeFilters: BookListFilterState = BookListFilterState(),
        val allGenres: List<Genre> = emptyList()
    )

    private val _filterState: MutableStateFlow<FilterState> = MutableStateFlow(
        FilterState()
    )

    private val _tempFilters: MutableStateFlow<BookListFilterState> =
        MutableStateFlow(BookListFilterState())
    val tempFilters = _tempFilters.asStateFlow()

    private val _searchQuery: MutableStateFlow<String> = MutableStateFlow("")

    private val _searchedBooksFlow: Flow<List<Book>> = combine(
        _books,
        _searchQuery
    ) { books, searchQuery ->
        if (searchQuery.isBlank()) {
            books
        } else {
            books.filter { book ->
                book.title.contains(searchQuery, ignoreCase = true) ||
                        book.author.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    val uiState: StateFlow<BookListUiState> = combine(
        _searchedBooksFlow,
        _userMessage,
        _isInitialLoading,
        _isRefreshing,
        _filterState
    ) { searchedBooks, userMessage, isInitialLoading, isRefreshing, filterState ->
        val activeFilters = filterState.activeFilters
        val filteredBooks = if (activeFilters.selectedGenreIds.isEmpty()) {
            searchedBooks
        } else {
            searchedBooks.filter { book ->
                book.genres.any { genre -> genre.id in activeFilters.selectedGenreIds }
            }
        }
        val sortedBooks = when (activeFilters.sortBy) {
            SortBy.TITLE -> filteredBooks.sortedBy { it.title }
            SortBy.AUTHOR -> filteredBooks.sortedBy { it.author }
            SortBy.DATE_ADDED -> filteredBooks.sortedBy { it.createdAt }
        }
        val finalBooks = if (activeFilters.sortOrder == SortOrder.DESCENDING) {
            sortedBooks.reversed()
        } else {
            sortedBooks
        }
        BookListUiState(
            books = finalBooks,
            userMessage = userMessage,
            isInitialLoading = isInitialLoading,
            isRefreshing = isRefreshing,
            isFilterSheetVisible = filterState.isFilterSheetVisible,
            activeFilters = activeFilters,
            allGenres = filterState.allGenres,
            searchQuery = _searchQuery.value
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = BookListUiState()
    )

    init {
        viewModelScope.launch {
            getGenresUseCase().collect { genres ->
                _filterState.update { it.copy(allGenres = genres) }
            }
        }
        refreshBooks(isPullToRefresh = false)
    }

    fun onRefresh() {
        refreshBooks(isPullToRefresh = true)
    }

    private fun refreshBooks(isPullToRefresh: Boolean) {
        viewModelScope.launch {
            if (isPullToRefresh) {
                _isRefreshing.value = true
            } else {
                _isInitialLoading.value = true
            }
            val result = refreshBooksUseCase()
            if (result.isFailure) {
                val exception = result.exceptionOrNull()
                _userMessage.value = "Ошибка при обновлении списка книг"
                Log.e("BookListViewModel", "Ошибка при обновлении списка книг", exception)
            }
            _isRefreshing.value = false
            _isInitialLoading.value = false
        }
    }

    fun userMessageShown() {
        _userMessage.value = null
    }

    fun onSearchQueryChanged(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun onFilterButtonClick() {
        _tempFilters.value = _filterState.value.activeFilters
        _filterState.update { it.copy(isFilterSheetVisible = true) }
    }

    fun onFilterSheetDismiss() {
        _filterState.update { it.copy(isFilterSheetVisible = false) }
    }

    fun onSortByChanged(newSortBy: SortBy) {
        _tempFilters.update { it.copy(sortBy = newSortBy) }
    }

    fun onSortOrderChanged(newSortOrder: SortOrder) {
        _tempFilters.update { it.copy(sortOrder = newSortOrder) }
    }

    fun onGenreSelectionChanged(genreId: Int, isSelected: Boolean) {
        _tempFilters.update {
            val newSet = _tempFilters.value.selectedGenreIds.toMutableSet()
            if (isSelected) newSet.add(genreId) else newSet.remove(genreId)
            it.copy(selectedGenreIds = newSet)
        }
    }

    fun onResetFiltersClick() {
        _tempFilters.value = BookListFilterState()
    }

    fun onApplyFiltersClick() {
        _filterState.update {
            it.copy(
                activeFilters = _tempFilters.value,
                isFilterSheetVisible = false
            )
        }
    }
}