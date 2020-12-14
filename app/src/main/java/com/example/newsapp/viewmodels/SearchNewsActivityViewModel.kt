package com.example.newsapp.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.newsapp.utils.NEWS_API_KEY
import com.example.newsapp.models.NewsResponse
import com.example.newsapp.services.ApiService
import com.example.newsapp.utils.STATUS_OK
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class SearchNewsActivityViewModel(private var client: Retrofit) : ViewModel() {
    var newsList = MutableLiveData<ArrayList<NewsResponse.Article>>()
    var isLoading: MutableLiveData<Boolean> = MutableLiveData(false)
    var isLastPage: MutableLiveData<Boolean> = MutableLiveData(false)
    var from: MutableLiveData<Int> = MutableLiveData(1)
    var isNewsError: MutableLiveData<Boolean> = MutableLiveData(false)

    fun getNews(token: String, country: String?, source: String?, pageSize: Int) {
        isLoading.value = true
        client.create(ApiService::class.java)
            .getNews(token, country, source, pageSize, from.value!!, NEWS_API_KEY)
            .enqueue(object : Callback<NewsResponse> {
                override fun onResponse(
                    call: Call<NewsResponse>,
                    response: Response<NewsResponse>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        if(response.body()!!.status == STATUS_OK) {
                            isNewsError.value = false
                            newsList.value = response.body()!!.articles
                            if (newsList.value!!.isEmpty()) {
                                isLastPage.value = true
                            } else {
                                from.value = from.value?.plus(1)
                            }
                        } else {
                            isNewsError.value = true
                        }
                    }
                    isNewsError.value = true
                    isLoading.value = false
                }

                override fun onFailure(call: Call<NewsResponse>, t: Throwable) {
                    Log.e("Failed", t.toString())
                    isLoading.value = false
                    isNewsError.value = true
                }
            }
            )
    }
}