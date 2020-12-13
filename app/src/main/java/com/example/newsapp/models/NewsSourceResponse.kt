package com.example.newsapp.models

data class NewsSourceResponse(
    val status: String,
    val sources: ArrayList<NewsSource>?
) {
    data class NewsSource(
        val id: String,
        val name: String
    )
}