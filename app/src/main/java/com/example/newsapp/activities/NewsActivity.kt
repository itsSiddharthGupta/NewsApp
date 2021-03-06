package com.example.newsapp.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AbsListView
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapp.R
import com.example.newsapp.adapters.NewsAdapter
import com.example.newsapp.databinding.ActivityNewsBinding
import com.example.newsapp.factory.RetrofitClient
import com.example.newsapp.factory.RetrofitClientViewModelFactory
import com.example.newsapp.fragments.CountryBottomSheetFragment
import com.example.newsapp.fragments.FilterSourcesBottomSheetFragment
import com.example.newsapp.models.NewsResponse
import com.example.newsapp.utils.FILTER_COUNTRY
import com.example.newsapp.utils.FILTER_SOURCE
import com.example.newsapp.utils.PAGE_SIZE
import com.example.newsapp.utils.checkConnection
import com.example.newsapp.viewmodels.NewsActivityViewModel
import kotlinx.android.synthetic.main.layout_no_internet.view.*

class NewsActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener,
    FilterSourcesBottomSheetFragment.ItemClickListener,
    CountryBottomSheetFragment.CountrySelectListener,
    NewsAdapter.OnNewsItemClickListener {
    private lateinit var binding: ActivityNewsBinding
    private lateinit var layoutManager: LinearLayoutManager
    private val adapter: NewsAdapter = NewsAdapter(this)
    private lateinit var viewModel: NewsActivityViewModel
    private var isScrolling: Boolean = false
    private var isLoading: Boolean = false
    private var isLastPage: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_news)
        layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = adapter
        binding.spinnerSort.onItemSelectedListener = this
        ArrayAdapter.createFromResource(
            this,
            R.array.sort_array,
            R.layout.sort_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(R.layout.sort_spinner_item)
            binding.spinnerSort.adapter = adapter
        }
        val client = RetrofitClient.retrofitClient
        val factory = RetrofitClientViewModelFactory(client!!)
        viewModel = ViewModelProvider(this, factory).get(NewsActivityViewModel::class.java)

        setListeners()
        setObservers()

        initialiseFlow()
    }

    private fun initialiseFlow() {
        if (checkConnection(this)) {
            binding.layoutNoInternet.visibility = View.GONE
            binding.layoutNews.visibility = View.VISIBLE
            binding.fabFilter.visibility = View.VISIBLE
            binding.locationLayout.isEnabled = true
            viewModel.getNews(FILTER_COUNTRY, FILTER_SOURCE, PAGE_SIZE)
        } else {
            binding.layoutNoInternet.visibility = View.VISIBLE
            binding.layoutNews.visibility = View.GONE
            binding.fabFilter.visibility = View.GONE
            binding.locationLayout.isEnabled = false
        }
    }

    private fun setListeners() {
        binding.searchNews.setOnClickListener {
            startActivity(Intent(this@NewsActivity, SearchNewsActivity::class.java))
        }

        binding.fabFilter.setOnClickListener {
            supportFragmentManager.let {
                FilterSourcesBottomSheetFragment.newInstance(Bundle()).apply {
                    show(it, tag)
                }
            }
        }

        binding.locationLayout.setOnClickListener {
            supportFragmentManager.let {
                CountryBottomSheetFragment.newInstance(Bundle()).apply {
                    show(it, tag)
                }
            }
        }

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

        binding.layoutNoInternet.btnRetry.setOnClickListener {
            initialiseFlow()
        }
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
                    Toast.makeText(this@NewsActivity, "Error loading news!", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        })
    }

    private fun fetchData() {
        if (FILTER_SOURCE != null) {
            viewModel.getNews(null, FILTER_SOURCE, PAGE_SIZE)
        } else {
            viewModel.getNews(FILTER_COUNTRY, FILTER_SOURCE, PAGE_SIZE)
        }
    }

    private fun refreshNews() {
        adapter.clearData();
        isScrolling = false
        viewModel.isLoading.value = false
        viewModel.isLastPage.value = false
        viewModel.from.value = 1
        fetchData()
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
        if (parent != null) {
            Log.e("spinner", parent.getItemAtPosition(pos).toString() + ", " + pos)
            when (pos) {
                0 -> adapter.sortByNewest()
                1 -> adapter.sortByNewest()
                2 -> adapter.sortByOldest()
            }
        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        TODO("Not yet implemented")
    }

    override fun onApplyFilter(item: ArrayList<String>) {
        if (item.isEmpty()) {
            FILTER_SOURCE = null
        } else {
            val builder = StringBuilder()
            item.forEach {
                builder.append(it).append(",")
            }
            FILTER_SOURCE = builder.toString()
        }
        refreshNews()
    }

    @SuppressLint("SetTextI18n")
    override fun onCountrySelected(item: String?) {
        FILTER_SOURCE = null
        FILTER_COUNTRY = item
        when (item) {
            "in" -> binding.txtLocation.text = "India"
            "us" -> binding.txtLocation.text = "United States"
            "au" -> binding.txtLocation.text = "Australia"
            "fr" -> binding.txtLocation.text = "France"
            "id" -> binding.txtLocation.text = "Indonesia"
        }
        refreshNews()
    }

    override fun onClick(article: NewsResponse.Article) {
        startActivity(
            Intent(this, NewsArticleActivity::class.java).putExtra(
                "news_article",
                article
            )
        )
    }
}