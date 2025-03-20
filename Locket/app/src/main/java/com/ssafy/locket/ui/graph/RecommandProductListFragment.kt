package com.ssafy.locket.ui.graph

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.ssafy.locket.BaseFragment
import com.ssafy.locket.R
import com.ssafy.locket.databinding.FragmentRecommandProductListBinding

class RecommandProductListFragment : BaseFragment<FragmentRecommandProductListBinding>(
    FragmentRecommandProductListBinding::bind,
    R.layout.fragment_recommand_product_list
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