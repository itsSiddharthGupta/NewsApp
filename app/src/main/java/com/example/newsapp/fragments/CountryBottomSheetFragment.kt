package com.example.newsapp.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import androidx.databinding.DataBindingUtil
import com.example.newsapp.FILTER_COUNTRY
import com.example.newsapp.R
import com.example.newsapp.databinding.LayoutCountryBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class CountryBottomSheetFragment : BottomSheetDialogFragment(), RadioGroup.OnCheckedChangeListener {
    private lateinit var binding: LayoutCountryBottomSheetBinding
    private var mListener: CountrySelectListener? = null
    private var countryId = FILTER_COUNTRY

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.layout_country_bottom_sheet,
                container,
                false
            )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fillSelectedCountry()
        binding.radioGroup.setOnCheckedChangeListener(this)
        binding.btnApply.setOnClickListener {
            mListener?.onCountrySelected(countryId)
            dismissAllowingStateLoss()
        }
    }

    private fun fillSelectedCountry(){
        when(countryId){
            "in" -> binding.radioIndia.isChecked = true
            "us" -> binding.radioUs.isChecked = true
            "au" -> binding.radioAus.isChecked = true
            "fr" -> binding.radioFrance.isChecked = true
            "id" -> binding.radioIndonesia.isChecked = true
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is CountrySelectListener) {
            mListener = context as CountrySelectListener
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

    interface CountrySelectListener {
        fun onCountrySelected(item: String?)
    }

    companion object {
        @JvmStatic
        fun newInstance(bundle: Bundle): CountryBottomSheetFragment {
            val fragment = CountryBottomSheetFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCheckedChanged(parent: RadioGroup?, pos: Int) {
        when (parent!!.checkedRadioButtonId) {
            R.id.radioIndia -> countryId = "in"
            R.id.radioAus -> countryId = "au"
            R.id.radioFrance -> countryId = "fr"
            R.id.radioIndonesia -> countryId = "id"
            R.id.radioUs -> countryId = "us"
        }
    }
}