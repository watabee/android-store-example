package com.github.watabee.storeexample.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import androidx.paging.PagedList
import com.github.watabee.storeexample.api.Article
import com.github.watabee.storeexample.paging.NetworkState
import com.github.watabee.storeexample.repository.DevRepository
import com.hadilq.liveevent.LiveEvent
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@OptIn(ExperimentalCoroutinesApi::class)
class ArticleViewModel @AssistedInject constructor(
    @Assisted articleTag: String,
    repositoryFactory: DevRepository.Factory
) : ViewModel() {

    @AssistedInject.Factory
    interface Factory {
        fun create(articleTag: String): ArticleViewModel
    }

    private val repository: DevRepository = repositoryFactory.create(articleTag)
    private val refreshEvent = LiveEvent<Unit>()

    val networkState: LiveData<NetworkState> = repository.networkState
    val articles: LiveData<PagedList<Article>> = repository.articles

    init {
        refreshEvent.asFlow()
            .onEach { repository.refresh() }
            .launchIn(viewModelScope)
    }

    fun retry() {
        repository.retry()
    }

    fun refresh() {
        refreshEvent.value = Unit
    }

    override fun onCleared() {
        super.onCleared()
        repository.cancel()
    }
}