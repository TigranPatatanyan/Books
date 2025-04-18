package com.tigran.books.di

import com.tigran.data.api.GoogleBooksApiService
import com.tigran.data.repository.BookRemoteRepositoryImpl
import com.tigran.domain.repository.BookRemoteRepository
import com.tigran.domain.usecase.SearchBooksUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SearchBooksUseCaseModule {

    private const val BASE_URL = "https://www.googleapis.com/books/v1/"

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Provides
    @Singleton
    fun provideGoogleBooksApi(retrofit: Retrofit): GoogleBooksApiService {
        return retrofit.create(GoogleBooksApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideBookRemoteRepository(
        api: GoogleBooksApiService
    ): BookRemoteRepository
    {
        return BookRemoteRepositoryImpl(api)
    }

    @Provides
    @Singleton
    fun provideSearchBooksUseCase(repository: BookRemoteRepository): SearchBooksUseCase =
        SearchBooksUseCase(repository)
}



