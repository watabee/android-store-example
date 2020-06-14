package com.github.watabee.storeexample.ui

import androidx.lifecycle.ViewModel
import androidx.paging.PagingData
import com.github.watabee.storeexample.api.Article
import com.github.watabee.storeexample.repository.DevRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
class ArticleViewModel(
    tag: String,
    repositoryFactory: DevRepository.Factory
) : ViewModel() {

    interface Factory {
        fun create(tag: String): ArticleViewModel
    }

    private val repository: DevRepository = repositoryFactory.create(tag)

    val articles: Flow<PagingData<Article>> = repository.pagingData
}

class ArticleViewModelFactory @Inject constructor(private val repositoryFactory: DevRepository.Factory) : ArticleViewModel.Factory {
    override fun create(tag: String): ArticleViewModel = ArticleViewModel(tag, repositoryFactory)
}
