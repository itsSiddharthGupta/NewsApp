package com.example.newsapp.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.example.newsapp.R
import com.example.newsapp.databinding.ActivityNewsArticleBinding
import com.example.newsapp.models.NewsResponse
import com.example.newsapp.utils.NOT_AVAILABLE
import com.example.newsapp.utils.convertDate

class NewsArticleActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNewsArticleBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_news_article)
        val extras = intent.extras
        val news: NewsResponse.Article? = extras?.getParcelable("news_article")
        inflateViewsWithData(news)
        binding.articleBack.setOnClickListener {
            onBackPressed()
        }
        binding.txtReadMore.setOnClickListener {
            if(news?.url != null)
                startActivity(Intent(this@NewsArticleActivity, FullArticleWebActivity::class.java).putExtra("NEWS_URL", news.url))
        }
    }

    private fun inflateViewsWithData(article: NewsResponse.Article?) {
        Glide.with(this).load(article?.urlToImage).into(binding.imgNews)
        if (article != null) {
            if (article.description != null)
                binding.txtNewsDesc.text = article.description
            else
                binding.txtNewsDesc.text = NOT_AVAILABLE
            if (article.content != null)
                binding.txtNewsContent.text = article.content
            else
                binding.txtNewsContent.text = NOT_AVAILABLE

            binding.txtNewsSource.text = article.source.name
            binding.txtNewsTime.text = convertDate(article.publishedAt!!)
        }
    }
}