package com.github.watabee.storeexample.di

import com.dropbox.android.external.store4.SourceOfTruth
import com.dropbox.android.external.store4.Store
import com.dropbox.android.external.store4.StoreBuilder
import com.dropbox.android.external.store4.nonFlowValueFetcher
import com.github.watabee.storeexample.api.Article
import com.github.watabee.storeexample.api.DevApi
import com.github.watabee.storeexample.api.DevConfig
import com.github.watabee.storeexample.db.ArticleDao
import com.github.watabee.storeexample.db.ArticleWithTags
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import timber.log.Timber
import javax.inject.Singleton
import kotlin.time.ExperimentalTime

@Module
abstract class StoreModule {

    companion object {
        @OptIn(ExperimentalTime::class, ExperimentalCoroutinesApi::class, FlowPreview::class)
        @Singleton
        @Provides
        fun provideStore(devApi: DevApi, articleDao: ArticleDao): Store<DevConfig, List<Article>> {
            return StoreBuilder.from<DevConfig, List<Article>, List<Article>>(
                nonFlowValueFetcher { config ->
                    Timber.e("nonFlowValueFetcher, config = $config")
                    val articles = devApi.findArticles(config.page, config.perPage, config.tag)
                    articles.forEach {
                        Timber.e("    $it")
                    }
                    articles
                },
                sourceOfTruth = SourceOfTruth.fromNonFlow(
                    reader = { config ->
                        Timber.e("reader, config = $config")
                        val offset = (config.page - 1) * config.perPage
                        val articles = articleDao.findArticles(offset, config.perPage, config.tag)
                        if (articles.isEmpty()) null else articles.map { it.toArticle() }
                    },
                    writer = { _, articles ->
                        Timber.e("writer")
                        articleDao.insert(articles)
                    },
                    delete = { config ->
                        Timber.e("delete")
                        articleDao.deleteByTag(config.tag)
                    },
                    deleteAll = {
                        Timber.e("deleteAll")
                        articleDao.deleteAll()
                    }
                )
            ).disableCache().build()
        }

        private fun ArticleWithTags.toArticle() =
            Article(
                id = article.id, title = article.title, description = article.description,
                publishedAt = article.publishedAt, tagList = tags
            )
    }
}