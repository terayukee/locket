package com.ssafy.locket.presentation.graph.fragments

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.ssafy.locket.model.graph.Product
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
        productList.add(Product(1000))
        productList.add(Product(2000))
        productList.add(Product(3000))
        productList.add(Product(3200))
        productList.add(Product(3100))
        productList.add(Product(3040))
        binding.rvRecommandList.layoutManager = GridLayoutManager(requireContext(), 3)
        binding.rvRecommandList.adapter = productAdapter
    }
}