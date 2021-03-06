package com.github.watabee.storeexample.db

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "articles",
    indices = [Index("articleId", "articleTag", unique = true), Index("articleTag")]
)
data class ArticleEntity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val articleId: Int,
    val title: String,
    val description: String,
    val publishedAt: Date,
    val articleTag: String
)