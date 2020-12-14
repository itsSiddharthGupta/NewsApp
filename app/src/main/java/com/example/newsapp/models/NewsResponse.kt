package com.example.newsapp.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

data class NewsResponse(
    val status: String,
    val code: String?,
    val message: String?,
    val totalResults: Int,
    val articles: ArrayList<Article>?
) {
    @Parcelize
    data class Article(
        val source: NewsSourceResponse.NewsSource,
        val author: String?,
        val title: String?,
        val description: String?,
        val url: String?,
        val urlToImage: String?,
        val publishedAt: String?,
        val content: String?
    ): Parcelable
}
