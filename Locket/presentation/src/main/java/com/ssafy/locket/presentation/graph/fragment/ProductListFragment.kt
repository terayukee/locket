package com.ssafy.locket.presentation.graph.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.ssafy.locket.model.graph.Product
import com.ssafy.locket.presentation.R
import com.ssafy.locket.presentation.base.BaseFragment
import com.ssafy.locket.presentation.databinding.FragmentProductListBinding
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

    //뒤로 가기 이벤트
    private var backPressedTime: Long = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initEvent()
        initAdapter()
        backEvent()
    }

    fun initEvent(){
        binding.ivHeart.setOnClickListener {
            findNavController().navigate(R.id.action_productListFragment_to_likeProductListFragment)
        }
        binding.ivInteriorMove.setOnClickListener {
            findNavController().navigate(R.id.action_productListFragment_to_categoryProductListFragment)
        }
        binding.btnRecommandMove.setOnClickListener {
            findNavController().navigate(R.id.action_productListFragment_to_recommendProductListFragment)
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

    fun backEvent(){
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (System.currentTimeMillis() - backPressedTime < 2000) {
                    requireActivity().finish() // 액티비티 종료
                } else {
                    backPressedTime = System.currentTimeMillis()
                    showToast("한 번 더 누르면 종료됩니다.")
                }
            }
        })
    }
}