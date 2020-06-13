package com.github.watabee.storeexample.paging

import androidx.paging.PagingSource
import com.dropbox.android.external.store4.Store
import com.dropbox.android.external.store4.StoreRequest
import com.dropbox.android.external.store4.StoreResponse
import com.github.watabee.storeexample.api.Article
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.first

class DevDataSource(
    private val tag: String,
    private val store: Store<DevConfig, List<Article>>
) : PagingSource<Int, Article>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Article> {
        val page = params.key ?: 1
        val config = DevConfig(page = page, perPage = params.loadSize, tag = tag)
        val storeRequest = StoreRequest.skipMemory(config, refresh = false)

        return try {
            val articles = store.stream(storeRequest)
                .filterNot { it is StoreResponse.Loading }
                .first()
                .requireData()

            LoadResult.Page(data = articles, prevKey = null, nextKey = page + 1)
        } catch (e: Throwable) {
            LoadResult.Error(e)
        }
    }
}
