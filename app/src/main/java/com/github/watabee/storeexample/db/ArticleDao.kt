package com.github.watabee.storeexample.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.github.watabee.storeexample.api.Article

@Dao
abstract class ArticleDao {

    @Transaction
    open suspend fun insert(articles: List<Article>) {
        insertArticles(articles.map { article -> ArticleEntity(0, article.id, article.title, article.description, article.publishedAt) })
        val tagNames = articles.flatMap { it.tagList }.distinct()
        insertTags(tagNames.map { TagEntity(0, it) })
        val tags = findTags(tagNames)
        val entities = articles.flatMap { article -> article.tagList.map { article.id to it } }
            .map { (articleId, tagName) -> ArticleTagCrossRefEntity(articleId, tags.find { it.tagName == tagName }!!.id) }
        insertArticleTags(entities)
    }

    @Transaction
    @Query("""
        SELECT articles.* FROM articles
        INNER JOIN article_tags ON articles.articleId = article_tags.articleId
        INNER JOIN tags ON article_tags.tagId = tags.id
        WHERE lower(tags.tagName) = lower(:tag)
        ORDER BY articles.id
        LIMIT :limit
        OFFSET :offset
    """)
    abstract suspend fun findArticles(offset: Int, limit: Int, tag: String): List<ArticleWithTags>

    @Query("SELECT * FROM tags WHERE tagName IN (:tagNames)")
    abstract suspend fun findTags(tagNames: List<String>): List<TagEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insertArticles(articles: List<ArticleEntity>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insertTags(tags: List<TagEntity>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insertArticleTags(articleTags: List<ArticleTagCrossRefEntity>)

    @Query("""
        DELETE FROM articles
        WHERE articles.articleId IN (
            SELECT articleId FROM article_tags
            INNER JOIN tags ON article_tags.tagId = tags.id
            WHERE lower(tags.tagName) = lower(:tag)
        )
    """)
    abstract suspend fun deleteArticles(tag: String)

    @Query("""
        DELETE FROM article_tags
        WHERE article_tags.tagId IN (SELECT id FROM tags WHERE lower(tags.tagName) = lower(:tag))
    """)
    abstract suspend fun deleteArticleTags(tag: String)

    @Transaction
    open suspend fun deleteByTag(tag: String) {
        deleteArticles(tag)
        deleteArticleTags(tag)
    }

    @Query("DELETE FROM articles")
    abstract suspend fun deleteArticles()

    @Query("DELETE FROM article_tags")
    abstract suspend fun deleteArticleTags()

    @Transaction
    open suspend fun deleteAll() {
        deleteArticles()
        deleteArticleTags()
    }
}