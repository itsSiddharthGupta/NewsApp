package com.example.newsapp.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.newsapp.services.ApiService
import com.example.newsapp.utils.NEWS_API_KEY
import com.example.newsapp.models.NewsResponse
import com.example.newsapp.models.NewsSourceResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class NewsActivityViewModel(private val client: Retrofit) : ViewModel() {
    var newsList = MutableLiveData<ArrayList<NewsResponse.Article>>()
    var sourceList = MutableLiveData<ArrayList<NewsSourceResponse.NewsSource>>()
    var isLoading: MutableLiveData<Boolean> = MutableLiveData(false)
    var isLastPage: MutableLiveData<Boolean> = MutableLiveData(false)
    var from: MutableLiveData<Int> = MutableLiveData(1)
    fun getNews(country: String?, source: String?, pageSize: Int) {
        isLoading.value = true
        client.create(ApiService::class.java)
            .getNews(null, country, source, pageSize, from.value!!, NEWS_API_KEY)
            .enqueue(object : Callback<NewsResponse> {
                override fun onResponse(
                    call: Call<NewsResponse>,
                    response: Response<NewsResponse>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        //todo take status into consideration
                        newsList.value = response.body()!!.articles
                        if (newsList.value!!.isEmpty()) {
                            isLastPage.value = true
                        } else {
                            from.value = from.value?.plus(1)
                        }
                    }
                    isLoading.value = false
                }

                override fun onFailure(call: Call<NewsResponse>, t: Throwable) {
                    Log.e("Failed", t.toString())
                    isLoading.value = false
                }
            }
            )
    }
    fun getSources(country: String?){
        client.create(ApiService::class.java).getSources(country, NEWS_API_KEY)
            .enqueue(object : Callback<NewsSourceResponse>{
                override fun onResponse(call: Call<NewsSourceResponse>, response: Response<NewsSourceResponse>) {
                    if(response.isSuccessful && response.body()!=null){
                        sourceList.value = response.body()!!.sources
                    }
                }

                override fun onFailure(call: Call<NewsSourceResponse>, t: Throwable) {
                    TODO("Not yet implemented")
                }
            })
    }
}