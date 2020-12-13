package com.example.newsapp.models

data class NewsResponse(
    val status: String,
    val code: String?,
    val message: String?,
    val totalResults: Int,
    val articles: ArrayList<Article>?
) {
    data class Article(
        val source: NewsSourceResponse.NewsSource,
        val author: String?,
        val title: String,
        val description: String,
        val url: String,
        val urlToImage: String,
        val publishedAt: String,
        val content: String
    )
}
