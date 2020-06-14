package com.github.watabee.storeexample.di

import com.dropbox.android.external.store4.SourceOfTruth
import com.dropbox.android.external.store4.Store
import com.dropbox.android.external.store4.StoreBuilder
import com.dropbox.android.external.store4.nonFlowValueFetcher
import com.github.watabee.storeexample.api.Article
import com.github.watabee.storeexample.api.DevApi
import com.github.watabee.storeexample.db.ArticleDao
import com.github.watabee.storeexample.db.ArticleEntity
import com.github.watabee.storeexample.paging.DevConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlin.time.ExperimentalTime

@Module
@InstallIn(ActivityRetainedComponent::class)
object StoreModule {

    @OptIn(ExperimentalTime::class, ExperimentalCoroutinesApi::class, FlowPreview::class)
    @ActivityRetainedScoped
    @Provides
    fun provideStore(devApi: DevApi, articleDao: ArticleDao): Store<DevConfig, List<Article>> {
        return StoreBuilder.from<DevConfig, List<Article>, List<Article>>(
            nonFlowValueFetcher { config ->
                devApi.findArticles(config.page, config.perPage, config.tag)
            },
            sourceOfTruth = SourceOfTruth.fromNonFlow(
                reader = { config ->
                    val offset = (config.page - 1) * config.perPage
                    val articles = articleDao.findArticles(offset, config.perPage, config.tag)
                    // Return null, then store fetch data using fetcher.
                    if (articles.isEmpty()) null else articles.map { it.toArticle() }
                },
                writer = { config, articles ->
                    articleDao.insertArticles(
                        articles.map { article ->
                            ArticleEntity(0, article.id, article.title, article.description, article.publishedAt, config.tag)
                        }
                    )
                },
                delete = { config ->
                    articleDao.deleteArticlesByTag(config.tag)
                },
                deleteAll = {
                    articleDao.deleteArticles()
                }
            )
        ).disableCache().build()
    }

    private fun ArticleEntity.toArticle() =
        Article(id = articleId, title = title, description = description, publishedAt = publishedAt, tagList = listOf(articleTag))
}