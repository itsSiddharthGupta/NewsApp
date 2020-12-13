package com.example.newsapp.services

import com.example.newsapp.models.NewsResponse
import com.example.newsapp.models.NewsSourceResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface ApiService {
    @GET("top-headlines/")
    fun getNews(
        @Query("country") country: String?,
        @Query("sources") sources: String?,
        @Query("pageSize") pageSize: Int,
        @Query("page") page: Int,
        @Header("Authorization") key: String
    ): Call<NewsResponse>

    @GET("sources/")
    fun getSources(
        @Query("country") country: String?,
        @Header("Authorization") key: String
    ): Call<NewsSourceResponse>
}