package com.github.watabee.storeexample.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.util.Date

@JsonClass(generateAdapter = true)
data class Article(
    @Json(name = "id") val id: Int,
    @Json(name = "title") val title: String,
    @Json(name = "description") val description: String,
    @Json(name = "tag_list") val tagList: List<String>,
    @Json(name = "published_at") val publishedAt: Date
)