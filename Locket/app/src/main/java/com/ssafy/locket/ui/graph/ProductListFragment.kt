package com.ssafy.locket.ui.graph

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.ssafy.locket.BaseFragment
import com.ssafy.locket.R
import com.ssafy.locket.databinding.FragmentProductListBinding

class ProductListFragment : BaseFragment<FragmentProductListBinding>(
    FragmentProductListBinding::bind,
    R.layout.fragment_product_list
) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initEvent()
    }

    fun initEvent(){
        binding.ivHeart.setOnClickListener {
            findNavController().navigate(R.id.action_productListFragment_to_likeProductListFragment)
        }
        binding.ivInteriorMove.setOnClickListener {
            findNavController().navigate(R.id.action_productListFragment_to_categoryProductListFragment)
        }
        binding.btnRecommandMove.setOnClickListener {
            findNavController().navigate(R.id.action_productListFragment_to_recommandProductListFragment)
        }
    }
}