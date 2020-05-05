package com.github.watabee.storeexample.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

@Dao
abstract class ArticleDao {

    @Transaction
    @Query("""
        SELECT articles.* FROM articles
        WHERE articles.articleTag = :tag
        ORDER BY articles.id
        LIMIT :limit
        OFFSET :offset
    """)
    abstract suspend fun findArticles(offset: Int, limit: Int, tag: String): List<ArticleEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insertArticles(articles: List<ArticleEntity>)

    @Query("DELETE FROM articles WHERE articleTag = :tag")
    abstract suspend fun deleteArticlesByTag(tag: String)

    @Query("DELETE FROM articles")
    abstract suspend fun deleteArticles()
}