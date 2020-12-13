package com.example.newsapp

import android.text.format.DateUtils
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

const val NEWS_API_BASE_URL = "https://newsapi.org/v2/"
const val NEWS_API_KEY = "d0f03357767249dba9395c94bfa3e1a9"
const val PAGE_SIZE: Int = 10
var FILTER_COUNTRY: String? = "in"
var FILTER_SOURCE: String? = null

fun convertDate(stringDate: String): String? {
    val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH)
    var formattedDate: String? = null
    try {
        val date = format.parse(stringDate)
        if (date != null) {
            formattedDate = DateUtils.getRelativeTimeSpanString(
                date.time,
                Calendar.getInstance().timeInMillis,
                DateUtils.MINUTE_IN_MILLIS
            )
                .toString()
        }
    } catch (e: ParseException) {
        e.printStackTrace()
    }
    return formattedDate
}