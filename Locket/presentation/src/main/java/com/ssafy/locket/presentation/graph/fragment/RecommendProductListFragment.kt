package com.ssafy.locket.presentation.graph.fragment

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.ssafy.locket.model.graph.Product
import com.ssafy.locket.model.graph.ProductxInfo
import com.ssafy.locket.presentation.R
import com.ssafy.locket.presentation.base.BaseFragment
import com.ssafy.locket.presentation.databinding.FragmentRecommendProductListBinding
import com.ssafy.locket.presentation.graph.adapter.ProductAdapter

class RecommendProductListFragment : BaseFragment<FragmentRecommendProductListBinding>(
    FragmentRecommendProductListBinding::bind,
    R.layout.fragment_recommend_product_list
) {

    private lateinit var productAdapter: ProductAdapter
    private lateinit var productList: MutableList<Product>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initEvent()
        initAdapter()
    }

    fun initEvent(){
        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    fun initAdapter(){
        productList = mutableListOf()
        productAdapter = ProductAdapter(productList,findNavController(),R.id.action_recommendProductListFragment_to_productDetailFragment)
        binding.rvRecommandList.layoutManager = GridLayoutManager(requireContext(), 3)
        binding.rvRecommandList.adapter = productAdapter
    }
}