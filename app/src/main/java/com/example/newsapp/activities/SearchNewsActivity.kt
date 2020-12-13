package com.example.newsapp.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.AbsListView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapp.FILTER_COUNTRY
import com.example.newsapp.FILTER_SOURCE
import com.example.newsapp.PAGE_SIZE
import com.example.newsapp.R
import com.example.newsapp.adapters.NewsAdapter
import com.example.newsapp.databinding.ActivitySearchNewsBinding
import com.example.newsapp.factory.RetrofitClient
import com.example.newsapp.factory.RetrofitClientViewModelFactory
import com.example.newsapp.models.NewsResponse
import com.example.newsapp.viewmodels.SearchNewsActivityViewModel

class SearchNewsActivity : AppCompatActivity(), NewsAdapter.OnNewsItemClickListener {
    private lateinit var binding: ActivitySearchNewsBinding
    private lateinit var layoutManager: LinearLayoutManager
    private val adapter: NewsAdapter = NewsAdapter(this)
    private lateinit var viewModel: SearchNewsActivityViewModel
    private var isScrolling: Boolean = false
    private var isLoading: Boolean = false
    private var isLastPage: Boolean = false
    private var token: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_search_news)
        layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = adapter
        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    isScrolling = true
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val currentItems = layoutManager.childCount
                val totalItems = layoutManager.itemCount
                val scrolledOutItems = layoutManager.findFirstVisibleItemPosition()
                Log.e("Pagination", "Total Items : $totalItems")
                Log.e("Pagination", "Current Items : $currentItems")
                Log.e("Pagination", "scrolled Items : $scrolledOutItems")
                if (!isLoading && !isLastPage) {
                    if (isScrolling
                        && (currentItems + scrolledOutItems >= totalItems)
                        && scrolledOutItems >= 0
                        && totalItems >= PAGE_SIZE
                    ) {
                        fetchData()
                    }
                }
            }
        })
        val client = RetrofitClient.retrofitClient
        val factory = RetrofitClientViewModelFactory(client!!)
        viewModel = ViewModelProvider(this, factory).get(SearchNewsActivityViewModel::class.java)
        viewModel.newsList.observe(this, {
            if (it != null) {
                adapter.fillData(it)
            }
        })

        viewModel.isLoading.observe(this, {
            if (it != null) {
                isLoading = it
                if (it) {
                    binding.progressBar.visibility = View.VISIBLE
                } else {
                    binding.progressBar.visibility = View.GONE
                }
            }
        })

        viewModel.isLastPage.observe(this, {
            if (it != null) {
                isLastPage = it
            }
        })

        binding.searchNews.setOnEditorActionListener { textView, id, keyEvent ->
            if (id == EditorInfo.IME_ACTION_DONE) {
                val keyword = textView.text.toString()
                refreshNews()
                if(keyword.isNotEmpty()){
                    token = keyword
                    fetchData()
                }
                binding.searchNews.isFocusable = false
                binding.searchNews.isFocusableInTouchMode = true
                hideKeyboard()
                return@setOnEditorActionListener true
            }
            false
        }
    }

    private fun fetchData() {
        if (FILTER_SOURCE != null) {
            viewModel.getNews(token, null, FILTER_SOURCE, PAGE_SIZE)
        } else {
            viewModel.getNews(token, FILTER_COUNTRY, FILTER_SOURCE, PAGE_SIZE)
        }
    }

    private fun refreshNews() {
        adapter.clearData();
        isScrolling = false
        viewModel.isLoading.value = false
        viewModel.isLastPage.value = false
        viewModel.from.value = 1
    }

    override fun onClick(article: NewsResponse.Article) {
        startActivity(
            Intent(this, NewsArticleActivity::class.java).putExtra(
                "news_article",
                article
            )
        )
    }

    fun hideKeyboard(){
        val imm: InputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.searchNews.windowToken, 0)
    }
}