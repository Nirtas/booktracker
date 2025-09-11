package ru.jerael.booktracker.android.domain.usecases.book

import android.net.Uri
import ru.jerael.booktracker.android.domain.model.appSuccess
import ru.jerael.booktracker.android.domain.storage.FileStorage
import java.io.File
import javax.inject.Inject

class SaveCoverFileUseCase @Inject constructor(
    private val fileStorage: FileStorage
) {
    suspend operator fun invoke(coverUri: Uri?): Result<File?> {
        return if (coverUri != null) {
            fileStorage.saveFile(coverUri)
        } else {
            appSuccess(null)
        }
    }
}