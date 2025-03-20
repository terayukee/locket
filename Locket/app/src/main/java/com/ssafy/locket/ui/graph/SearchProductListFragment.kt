package com.ssafy.locket.ui.graph

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.ssafy.locket.BaseFragment
import com.ssafy.locket.R
import com.ssafy.locket.databinding.FragmentProductListBinding
import com.ssafy.locket.databinding.FragmentSearchProductListBinding

class SearchProductListFragment : BaseFragment<FragmentSearchProductListBinding>(
    FragmentSearchProductListBinding::bind,
    R.layout.fragment_search_product_list
) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initEvent()
    }

    fun initEvent(){
        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }
}