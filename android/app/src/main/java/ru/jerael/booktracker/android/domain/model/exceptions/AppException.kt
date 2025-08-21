package ru.jerael.booktracker.android.domain.model.exceptions

import ru.jerael.booktracker.android.domain.model.AppError

class AppException(val appError: AppError) : Exception()