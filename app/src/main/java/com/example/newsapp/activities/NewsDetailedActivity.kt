package com.example.newsapp.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.example.newsapp.R
import com.example.newsapp.convertDate
import com.example.newsapp.databinding.ActivityNewsDetailedBinding
import com.example.newsapp.models.NewsResponse

class NewsDetailedActivity: AppCompatActivity() {
    private lateinit var binding: ActivityNewsDetailedBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_news_detailed)
        val extras = intent.extras
        val news: NewsResponse.Article? = extras?.getParcelable("news_article")
        inflateViewsWithData(news)
        binding.articleBack.setOnClickListener{
            onBackPressed()
        }
    }

    private fun inflateViewsWithData(article: NewsResponse.Article?) {
        Glide.with(this).load(article?.urlToImage).into(binding.imgNews)
        binding.txtNewsDesc.text = article?.description
        binding.txtNewsContent.text = article?.content
        binding.txtNewsSource.text = article?.source?.name
        binding.txtNewsTime.text = convertDate(article?.publishedAt!!)
    }
}