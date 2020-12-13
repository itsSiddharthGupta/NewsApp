package com.example.newsapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapp.FILTER_SOURCE
import com.example.newsapp.models.NewsSourceResponse
import com.example.newsapp.R

class SourceAdapter(var listener: OnSourceSelectListener) :
    RecyclerView.Adapter<SourceAdapter.MyViewHolder>() {

    private var sourceList = ArrayList<NewsSourceResponse.NewsSource>()

    fun fillData(sourceList: ArrayList<NewsSourceResponse.NewsSource>) {
        this.sourceList = sourceList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.source_item, null)
        val params = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        view.layoutParams = params
        return MyViewHolder(view, listener)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindView(sourceList[position])
    }

    override fun getItemCount() = sourceList.size

    class MyViewHolder(itemView: View,var listener: OnSourceSelectListener) :
        RecyclerView.ViewHolder(itemView) {
        private val checkBox = itemView.findViewById<CheckBox>(R.id.sourceCheckBox)

        fun bindView(source: NewsSourceResponse.NewsSource) {
            checkBox.text = source.name
            if(FILTER_SOURCE !=null && FILTER_SOURCE!!.contains(source.id)){
                checkBox.isChecked = true
            }
            checkBox.setOnCheckedChangeListener { button, b ->
                if (b) {
                    listener.onSourceSelected(source.id)
                } else {
                    listener.onSourceDeselected(source.id)
                }
            }
        }
    }

    interface OnSourceSelectListener {
        fun onSourceSelected(source: String)
        fun onSourceDeselected(source: String)
    }
}