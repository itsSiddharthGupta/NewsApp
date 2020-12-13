package com.example.newsapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.newsapp.models.NewsResponse
import com.example.newsapp.R
import com.example.newsapp.convertDate

class NewsAdapter(var listener: OnNewsItemClickListener) : RecyclerView.Adapter<NewsAdapter.MyViewHolder>() {

    private var newsList = ArrayList<NewsResponse.Article>()
    private var sortInd = 1

    fun fillData(newsList: ArrayList<NewsResponse.Article>) {
        this.newsList.addAll(newsList)
        when (sortInd) {
            1 -> sortByNewest()
            2 -> sortByOldest()
        }
    }

    fun sortByOldest() {
        sortInd = 2
        this.newsList.sortBy { it.publishedAt }
        notifyDataSetChanged()
    }

    fun sortByNewest() {
        sortInd = 1
        this.newsList.sortByDescending { it.publishedAt }
        notifyDataSetChanged()
    }

    fun clearData() {
        this.newsList = ArrayList()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.news_item_card, null)
        val params = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        view.layoutParams = params
        return MyViewHolder(view, listener)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindView(newsList[position])
    }

    override fun getItemCount() = newsList.size

    class MyViewHolder(itemView: View, var listener: OnNewsItemClickListener) : RecyclerView.ViewHolder(itemView) {
        private val textNewsSource = itemView.findViewById<TextView>(R.id.txtNewsSource)
        private val textNewsDesc = itemView.findViewById<TextView>(R.id.txtNewsDesc)
        private val textNewsTime = itemView.findViewById<TextView>(R.id.txtNewsTime)
        private val imgNews = itemView.findViewById<ImageView>(R.id.imgNews)

        fun bindView(article: NewsResponse.Article) {
            textNewsSource.text = article.source.name
            textNewsDesc.text = article.description
            textNewsTime.text = convertDate(article.publishedAt)
            Glide.with(itemView.context).load(article.urlToImage).into(imgNews)
            itemView.setOnClickListener {
                listener.onClick(article)
            }
        }
    }

    interface OnNewsItemClickListener{
        fun onClick(article: NewsResponse.Article)
    }
}