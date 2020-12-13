package com.example.newsapp.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

data class NewsSourceResponse(
    val status: String,
    val sources: ArrayList<NewsSource>?
) {
    @Parcelize
    data class NewsSource(
        val id: String?,
        val name: String
    ): Parcelable
}