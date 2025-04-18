package com.tigran.books.di

import android.content.Context
import androidx.room.Room
import com.tigran.data.db.BookDao
import com.tigran.data.db.BookDatabase
import com.tigran.data.repository.BookLocalRepositoryImpl
import com.tigran.domain.repository.BookLocalRepository
import com.tigran.domain.usecase.LocalUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocalUseCaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): BookDatabase {
        return Room.databaseBuilder(
            context,
            BookDatabase::class.java,
            "books.db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideBookDao(db: BookDatabase): BookDao = db.bookDao()

    @Provides
    fun provideJson(): Json = Json { ignoreUnknownKeys = true }

    @Provides
    @Singleton
    fun provideBookLocalRepository(dao: BookDao): BookLocalRepository =
        BookLocalRepositoryImpl(dao)

    @Provides
    @Singleton
    fun provideLocalUseCase(repository: BookLocalRepository): LocalUseCase =
        LocalUseCase(repository)
}