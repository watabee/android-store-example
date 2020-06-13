package com.github.watabee.storeexample.ui

import androidx.lifecycle.ViewModel
import androidx.paging.PagingData
import com.github.watabee.storeexample.api.Article
import com.github.watabee.storeexample.repository.DevRepository
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow

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

    val articles: Flow<PagingData<Article>> = repository.pagingData
}