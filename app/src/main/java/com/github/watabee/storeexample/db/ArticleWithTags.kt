package com.github.watabee.storeexample.db

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class ArticleWithTags(
    @Embedded val article: ArticleEntity,
    @Relation(
        parentColumn = "articleId",
        entity = TagEntity::class,
        entityColumn = "id",
        associateBy = Junction(ArticleTagCrossRefEntity::class, parentColumn = "articleId", entityColumn = "tagId"),
        projection = ["tagName"]
    )
    val tags: List<String>
)