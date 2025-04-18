package com.tigran.books.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.tigran.data.worker.BookCachingWorkerHelper
import com.tigran.domain.model.Book
import com.tigran.domain.usecase.LocalUseCase
import com.tigran.domain.usecase.SearchBooksUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

@HiltViewModel
class BookViewModel @Inject constructor(
    private val searchBooksUseCase: SearchBooksUseCase, private val localUseCase: LocalUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(BookUiState())
    val uiState: StateFlow<BookUiState> = _uiState.asStateFlow()

    private val queryFlow = _uiState.map { it.query }

    val books: Flow<PagingData<Book>> =
        queryFlow.debounce(300).distinctUntilChanged().flatMapLatest { query ->
            searchBooksUseCase(query)
        }

    fun handleIntent(intent: BookIntent) {
        when (intent) {
            is BookIntent.Search -> {
                _uiState.update {
                    it.copy(
                        query = intent.query,
                        loadingState = LoadingState.RemoteBooksLoadingState.Loading
                    )
                }
            }

            is BookIntent.AddToFavorites -> _uiState.update {
                it.copy(
                    favorites = it.favorites.toMutableSet().apply { add(intent.book) })
            }

            BookIntent.LoadFromLocal -> {
                getFromLocal()
            }

            is BookIntent.DownloadAllFavorites -> {
                downloadAllFavorites(intent.context)
            }

            is BookIntent.BookDetails -> _uiState.update { it.copy(book = intent.book) }
            BookIntent.ResetLoadingState -> _uiState.update { it.copy(loadingState = null) }
            BookIntent.NotifyNetworkError -> _uiState.update { it.copy(loadingState = LoadingState.RemoteBooksLoadingState.NoNetwork) }
        }
    }

    private fun downloadAllFavorites(context: Context) {
        val workId = BookCachingWorkerHelper.enqueue(
            context.applicationContext, _uiState.value.favorites.toList()
        )
        viewModelScope.launch {
            WorkManager.getInstance(context).getWorkInfoByIdFlow(workId).collect { info ->
                when (info?.state) {
                    WorkInfo.State.ENQUEUED -> {}
                    WorkInfo.State.SUCCEEDED -> {
                        _uiState.update { it.copy(loadingState = LoadingState.WorkerDownloadState.Success) }
                    }

                    WorkInfo.State.FAILED -> {
                        _uiState.update { it.copy(loadingState = LoadingState.WorkerDownloadState.Failed) }
                    }

                    WorkInfo.State.CANCELLED -> {}
                    WorkInfo.State.RUNNING -> {
                        _uiState.update { it.copy(loadingState = LoadingState.WorkerDownloadState.Downloading) }
                    }

                    WorkInfo.State.BLOCKED -> {}
                    null -> {}
                }
            }
        }
    }

    private fun getFromLocal() {
        viewModelScope.launch {
            _uiState.update { it.copy(loadingState = LoadingState.LocalFavoriteBooksLoadingState.Loading) }
            val cachedData = withContext(Dispatchers.IO) {
                localUseCase.getCachedData().toMutableSet()
            }
            _uiState.update {
                val updatedFavorites = it.favorites.toMutableSet().apply {
                    addAll(cachedData)
                }
                it.copy(
                    favorites = updatedFavorites,
                    loadingState = if (updatedFavorites.isEmpty()) LoadingState.LocalFavoriteBooksLoadingState.NotFound else LoadingState.LocalFavoriteBooksLoadingState.Loaded
                )
            }
        }
    }
}

data class BookUiState(
    val query: String = "",
    val searchQuery: String = "",
    val favorites: MutableSet<Book> = mutableSetOf(),
    val book: Book? = null,
    val error: Error? = null,
    val loadingState: LoadingState? = null
)

sealed interface LoadingState {

    sealed interface RemoteBooksLoadingState : LoadingState {
        data object Loading : RemoteBooksLoadingState
        data object Loaded : RemoteBooksLoadingState
        data object NoNetwork : RemoteBooksLoadingState
    }

    sealed interface WorkerDownloadState : LoadingState {
        data object Downloading : WorkerDownloadState
        data object Success : WorkerDownloadState
        data object Failed : WorkerDownloadState
    }

    sealed interface LocalFavoriteBooksLoadingState : LoadingState {
        data object Loading : LocalFavoriteBooksLoadingState
        data object Loaded : LocalFavoriteBooksLoadingState
        data object NotFound : LocalFavoriteBooksLoadingState
    }
}


sealed class BookIntent {
    data class Search(val query: String) : BookIntent()
    data class AddToFavorites(val book: Book) : BookIntent()
    data class BookDetails(val book: Book) : BookIntent()
    data class DownloadAllFavorites(val context: Context) : BookIntent()
    data object LoadFromLocal : BookIntent()
    data object ResetLoadingState : BookIntent()
    data object NotifyNetworkError : BookIntent()
}