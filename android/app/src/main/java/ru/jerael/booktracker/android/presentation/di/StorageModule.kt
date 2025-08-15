package ru.jerael.booktracker.android.presentation.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.jerael.booktracker.android.data.storage.FileStorageImpl
import ru.jerael.booktracker.android.domain.storage.FileStorage
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class StorageModule {

    @Binds
    @Singleton
    abstract fun bindFileStorage(fileStorageImpl: FileStorageImpl): FileStorage
}