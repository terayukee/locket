package com.ssafy.locket.presentation.graph

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.ssafy.locket.BaseFragment
import com.ssafy.locket.R
import com.ssafy.locket.data.remote.dto.Product
import com.ssafy.locket.databinding.FragmentProductListBinding
import com.ssafy.locket.presentation.graph.adapter.MoneyHappyAdapter
import com.ssafy.locket.presentation.graph.adapter.ProductAdapter

class ProductListFragment : BaseFragment<FragmentProductListBinding>(
    FragmentProductListBinding::bind,
    R.layout.fragment_product_list
) {
    private lateinit var productAdapter: ProductAdapter
    private lateinit var moneyHappyLisAdapter: MoneyHappyAdapter
    private lateinit var productList: MutableList<Product>
    private lateinit var moneyHappyList: MutableList<Product>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initEvent()
        initAdapter()
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

    fun initAdapter(){
        productList = mutableListOf()
        moneyHappyList = mutableListOf()
        productAdapter = ProductAdapter(productList,findNavController(),R.id.action_productListFragment_to_productDetailFragment)
        moneyHappyLisAdapter = MoneyHappyAdapter(moneyHappyList,findNavController())
        productList.add(Product(1000))
        productList.add(Product(2000))
        productList.add(Product(3000))
        productList.add(Product(3200))
        productList.add(Product(3100))
        productList.add(Product(3040))
        moneyHappyList.add(Product(1000))
        moneyHappyList.add(Product(1000))
        moneyHappyList.add(Product(1000))
        moneyHappyList.add(Product(1000))
        moneyHappyList.add(Product(1000))
        moneyHappyList.add(Product(1000))
        moneyHappyList.add(Product(1000))
        moneyHappyList.add(Product(1000))
        binding.rvRecommanditemList.layoutManager = GridLayoutManager(requireContext(), 3)
        binding.rvRecommanditemList.adapter = productAdapter
        binding.rvMoneyHappyList.layoutManager = LinearLayoutManager(requireContext())
        binding.rvMoneyHappyList.adapter = moneyHappyLisAdapter
    }
}