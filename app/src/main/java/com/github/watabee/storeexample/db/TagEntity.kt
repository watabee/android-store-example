package com.github.watabee.storeexample.db

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "tags", indices = [Index("tagName", unique = true)])
data class TagEntity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val tagName: String
)
