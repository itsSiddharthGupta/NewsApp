package com.example.newsapp.utils

import android.content.Context
import android.net.ConnectivityManager
import android.text.format.DateUtils
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


const val NEWS_API_BASE_URL = "https://newsapi.org/v2/"
const val NEWS_API_KEY = "7718718fc623405a8d365536e9fe3ffe"
const val PAGE_SIZE: Int = 10
const val NOT_AVAILABLE = "Not available"
const val STATUS_OK = "ok"
var FILTER_COUNTRY: String? = "in"
var FILTER_SOURCE: String? = null

fun checkConnection(context: Context): Boolean {
    val connMgr = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetworkInfo = connMgr.activeNetworkInfo
    if (activeNetworkInfo != null) {
        return if (activeNetworkInfo.type == ConnectivityManager.TYPE_WIFI) {
            true
        } else activeNetworkInfo.type == ConnectivityManager.TYPE_MOBILE
    }
    return false
}

fun convertDate(stringDate: String?): String? {
    val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH)
    var formattedDate: String? = null
    if (stringDate != null) {
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
    }
    return formattedDate
}
