package com.github.watabee.storeexample.db

import androidx.room.Entity
import androidx.room.Index

@Entity(tableName = "article_tags", primaryKeys = ["articleId", "tagId"], indices = [Index("tagId")])
data class ArticleTagCrossRefEntity(
    val articleId: Int,
    val tagId: Int
)
