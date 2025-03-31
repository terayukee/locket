package com.ssafy.locket.presentation.graph.fragment

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.ssafy.locket.presentation.R
import com.ssafy.locket.presentation.base.BaseFragment
import com.ssafy.locket.presentation.databinding.FragmentLikeProductListBinding

class LikeProductListFragment : BaseFragment<FragmentLikeProductListBinding>(
    FragmentLikeProductListBinding::bind,
    R.layout.fragment_like_product_list
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