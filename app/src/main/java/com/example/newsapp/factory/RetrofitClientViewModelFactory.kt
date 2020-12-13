package com.example.newsapp.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import retrofit2.Retrofit

class RetrofitClientViewModelFactory(private val client: Retrofit) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(Retrofit::class.java).newInstance(client)
    }
}