package com.example.newsapp.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.AbsListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapp.R
import com.example.newsapp.adapters.NewsAdapter
import com.example.newsapp.databinding.ActivitySearchNewsBinding
import com.example.newsapp.factory.RetrofitClient
import com.example.newsapp.factory.RetrofitClientViewModelFactory
import com.example.newsapp.models.NewsResponse
import com.example.newsapp.utils.FILTER_COUNTRY
import com.example.newsapp.utils.FILTER_SOURCE
import com.example.newsapp.utils.PAGE_SIZE
import com.example.newsapp.utils.checkConnection
import com.example.newsapp.viewmodels.SearchNewsActivityViewModel
import kotlinx.android.synthetic.main.layout_no_internet.view.*

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

        setObservers()
        setListeners()

        initialiseFlow()

        requestFocus()
    }

    private fun initialiseFlow() {
        if (checkConnection(this)) {
            binding.layoutNoInternet.visibility = View.GONE
            binding.layoutNews.visibility = View.VISIBLE
            binding.searchNews.visibility = View.VISIBLE
        } else {
            binding.layoutNoInternet.visibility = View.VISIBLE
            binding.layoutNews.visibility = View.GONE
            binding.searchNews.visibility = View.GONE
        }
    }

    private fun setListeners() {
        binding.searchNews.setOnEditorActionListener { textView, id, keyEvent ->
            if (id == EditorInfo.IME_ACTION_DONE) {
                val keyword = textView.text.toString()
                refreshNews()
                if (keyword.isNotEmpty()) {
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
        binding.searchBack.setOnClickListener { onBackPressed() }
        binding.layoutNoInternet.btnRetry.setOnClickListener { initialiseFlow() }
    }

    private fun setObservers() {
        viewModel.newsList.observe(this, {
            if (it != null) {
                adapter.fillData(it)
                if (adapter.itemCount == 0) {
                    binding.layoutEmptyList.visibility = View.VISIBLE
                    binding.layoutNews.visibility = View.GONE
                } else {
                    binding.layoutEmptyList.visibility = View.GONE
                    binding.layoutNews.visibility = View.VISIBLE
                }
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

        viewModel.isNewsError.observe(this, {
            if (it != null) {
                if (it) {
                    Toast.makeText(
                        this@SearchNewsActivity,
                        "Error loading news!",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            }
        })
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

    private fun requestFocus() {
        binding.searchNews.requestFocus()
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(binding.searchNews, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun hideKeyboard() {
        val imm: InputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.searchNews.windowToken, 0)
    }
}