package com.ssafy.locket.ui.graph

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.ssafy.locket.BaseFragment
import com.ssafy.locket.R
import com.ssafy.locket.data.remote.dto.Product
import com.ssafy.locket.databinding.FragmentRecommandProductListBinding
import com.ssafy.locket.ui.graph.adapter.ProductAdapter

class RecommandProductListFragment : BaseFragment<FragmentRecommandProductListBinding>(
    FragmentRecommandProductListBinding::bind,
    R.layout.fragment_recommand_product_list
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
        productAdapter = ProductAdapter(productList,findNavController(),R.id.action_recommandProductListFragment_to_productDetailFragment)
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