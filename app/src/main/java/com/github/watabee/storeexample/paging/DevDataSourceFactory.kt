package com.github.watabee.storeexample.paging

import androidx.paging.DataSource
import com.dropbox.android.external.store4.Store
import com.github.watabee.storeexample.api.Article
import com.github.watabee.storeexample.api.DevConfig

class DevDataSourceFactory(
    private val tag: String,
    private val store: Store<DevConfig, List<Article>>
) : DataSource.Factory<Int, Article>() {

    private var dataSource: DevDataSource? = null

    override fun create(): DataSource<Int, Article> {
        val devDataSource = DevDataSource(tag, store)
        dataSource = devDataSource
        return devDataSource
    }

    fun invalidate() {
        dataSource?.invalidate()
    }

    fun cancel() {
        dataSource?.cancel()
    }
}
