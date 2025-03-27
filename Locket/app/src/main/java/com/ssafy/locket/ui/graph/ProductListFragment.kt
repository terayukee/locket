package com.ssafy.locket.ui.graph

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.ssafy.locket.BaseFragment
import com.ssafy.locket.R
import com.ssafy.locket.data.remote.dto.Product
import com.ssafy.locket.databinding.FragmentProductListBinding
import com.ssafy.locket.ui.graph.adapter.MoneyHappyAdapter
import com.ssafy.locket.ui.graph.adapter.ProductAdapter

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
        binding.etProductSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                binding.ivClear.visibility = if (s.isNullOrEmpty()) View.INVISIBLE else View.VISIBLE
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
        binding.ivClear.setOnClickListener {
            binding.etProductSearch.setText("")
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