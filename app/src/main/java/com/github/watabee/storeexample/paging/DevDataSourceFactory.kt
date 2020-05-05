package com.github.watabee.storeexample.paging

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.dropbox.android.external.store4.Store
import com.github.watabee.storeexample.api.Article
import com.github.watabee.storeexample.api.DevConfig

class DevDataSourceFactory(
    private val tag: String,
    private val store: Store<DevConfig, List<Article>>
) : DataSource.Factory<Int, Article>() {

    private val _dataSource = MutableLiveData<DevDataSource>()
    val dataSource: LiveData<DevDataSource> = _dataSource

    override fun create(): DataSource<Int, Article> {
        val devDataSource = DevDataSource(tag, store)
        _dataSource.postValue(devDataSource)
        return devDataSource
    }
}
