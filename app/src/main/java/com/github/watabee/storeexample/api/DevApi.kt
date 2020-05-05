package com.github.watabee.storeexample.api

import retrofit2.http.GET
import retrofit2.http.Query

interface DevApi {
    @GET("articles")
    suspend fun findArticles(
        @Query("page") page: Int,
        @Query("per_page") perPage: Int,
        @Query("tag") tag: String
    ): List<Article>
}