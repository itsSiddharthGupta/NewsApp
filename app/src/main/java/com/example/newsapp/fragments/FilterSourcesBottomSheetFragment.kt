package com.example.newsapp.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newsapp.FILTER_COUNTRY
import com.example.newsapp.R
import com.example.newsapp.adapters.SourceAdapter
import com.example.newsapp.databinding.LayoutFilterBottomSheetBinding
import com.example.newsapp.factory.RetrofitClientViewModelFactory
import com.example.newsapp.factory.RetrofitClient
import com.example.newsapp.viewmodels.NewsActivityViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class FilterSourcesBottomSheetFragment : BottomSheetDialogFragment(), SourceAdapter.OnSourceSelectListener {
    private lateinit var viewModel: NewsActivityViewModel
    private lateinit var binding: LayoutFilterBottomSheetBinding
    private var adapter = SourceAdapter(this)
    private var srcList = ArrayList<String>()
    private var mListener: ItemClickListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.layout_filter_bottom_sheet, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val client = RetrofitClient.retrofitClient
        val factory = RetrofitClientViewModelFactory(client!!)
        viewModel = ViewModelProvider(this, factory).get(NewsActivityViewModel::class.java)
        viewModel.getSources(FILTER_COUNTRY)
        binding.recyclerViewFilter.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.recyclerViewFilter.adapter = adapter
        viewModel.sourceList.observe(viewLifecycleOwner, {
            if (it != null) {
                adapter.fillData(it)
            }
        })
        binding.btnApplyFilter.setOnClickListener{
            mListener?.onApplyFilter(srcList)
            dismissAllowingStateLoss()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is ItemClickListener) {
            mListener = context as ItemClickListener
        } else {
            throw RuntimeException(
                "$context must implement ItemClickListener"
            )
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    interface ItemClickListener {
        fun onApplyFilter(item: ArrayList<String>)
    }

    companion object {
        @JvmStatic
        fun newInstance(bundle: Bundle): FilterSourcesBottomSheetFragment {
            val fragment = FilterSourcesBottomSheetFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onSourceSelected(source: String) {
        srcList.add(source)
    }

    override fun onSourceDeselected(source: String) {
        srcList.remove(source)
    }
}