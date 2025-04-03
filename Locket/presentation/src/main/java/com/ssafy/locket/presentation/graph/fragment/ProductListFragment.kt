package com.ssafy.locket.presentation.graph.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.ssafy.locket.model.graph.Product
import com.ssafy.locket.model.graph.ProductxInfo
import com.ssafy.locket.presentation.R
import com.ssafy.locket.presentation.base.BaseFragment
import com.ssafy.locket.presentation.databinding.FragmentProductListBinding
import com.ssafy.locket.presentation.graph.adapter.MoneyHappyAdapter
import com.ssafy.locket.presentation.graph.adapter.ProductAdapter
import com.ssafy.locket.presentation.graph.viewmodel.ProductHappyListState
import com.ssafy.locket.presentation.graph.viewmodel.ProductLikeListState
import com.ssafy.locket.presentation.graph.viewmodel.ProductViewModel
import com.ssafy.locket.presentation.utils.CommonUtils
import com.ssafy.locket.presentation.utils.ToastType
import kotlinx.coroutines.launch

private const val TAG = "LikeProductListFragment"
class ProductListFragment : BaseFragment<FragmentProductListBinding>(
    FragmentProductListBinding::bind,
    R.layout.fragment_product_list
) {
    private lateinit var productAdapter: ProductAdapter
    private lateinit var moneyHappyListAdapter: MoneyHappyAdapter
    private lateinit var productList: MutableList<ProductxInfo>
    private lateinit var moneyHappyList: MutableList<Product>

    private val productViewModel: ProductViewModel by activityViewModels()
    //뒤로 가기 이벤트
    private var backPressedTime: Long = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initCategoryMoveEvent()
        getHappyListData()
        initEvent()
        initAdapter()
        backEvent()
    }

    fun initEvent(){
        binding.ivHeart.setOnClickListener {
            findNavController().navigate(R.id.action_productListFragment_to_likeProductListFragment)
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
        productViewModel.getHappyList()
    }

    fun initCategoryMoveEvent(){
        binding.ivInteriorMove.setOnClickListener {
            val bundle = Bundle().apply { putInt("productId", 3) }
            findNavController().navigate(R.id.action_productListFragment_to_categoryProductListFragment, bundle)
        }
        binding.ivFoodMove.setOnClickListener {
            val bundle = Bundle().apply { putInt("productId", 1) }
            findNavController().navigate(R.id.action_productListFragment_to_categoryProductListFragment, bundle)
        }
        binding.ivHouseholditemsMove.setOnClickListener {
            val bundle = Bundle().apply { putInt("productId", 2) }
            findNavController().navigate(R.id.action_productListFragment_to_categoryProductListFragment, bundle)
        }
        binding.ivToysMove.setOnClickListener {
            val bundle = Bundle().apply { putInt("productId", 8) }
            findNavController().navigate(R.id.action_productListFragment_to_categoryProductListFragment, bundle)
        }
        binding.ivAppliancesMove.setOnClickListener {
            val bundle = Bundle().apply { putInt("productId", 4) }
            findNavController().navigate(R.id.action_productListFragment_to_categoryProductListFragment, bundle)
        }
        binding.ivKitchenwareMove.setOnClickListener {
            val bundle = Bundle().apply { putInt("productId", 5) }
            findNavController().navigate(R.id.action_productListFragment_to_categoryProductListFragment, bundle)
        }
        binding.ivBabyproductsMove.setOnClickListener {
            val bundle = Bundle().apply { putInt("productId", 6) }
            findNavController().navigate(R.id.action_productListFragment_to_categoryProductListFragment, bundle)
        }
        binding.ivPetMove.setOnClickListener {
            val bundle = Bundle().apply { putInt("productId", 7) }
            findNavController().navigate(R.id.action_productListFragment_to_categoryProductListFragment, bundle)
        }
        binding.ivStationeryMove.setOnClickListener {
            val bundle = Bundle().apply { putInt("productId", 9) }
            findNavController().navigate(R.id.action_productListFragment_to_categoryProductListFragment, bundle)
        }
        binding.ivHealthMove.setOnClickListener {
            val bundle = Bundle().apply { putInt("productId", 10) }
            findNavController().navigate(R.id.action_productListFragment_to_categoryProductListFragment, bundle)
        }
    }
    fun getHappyListData(){
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                productViewModel.productHappyListInfo.collect { productHappyList ->
                    Log.d(TAG,productHappyList.toString())
                    if(productHappyList is ProductHappyListState.Success) {
                        Log.d(TAG,"만원의 행복")
                        Log.d(TAG,productHappyList.productHappyListInfo.toString())
                        moneyHappyList.clear()
                        moneyHappyList.addAll(productHappyList.productHappyListInfo.products)
                        moneyHappyListAdapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }
    fun initAdapter(){
        productList = mutableListOf()
        moneyHappyList = mutableListOf()
        productAdapter = ProductAdapter(productList,findNavController(),R.id.action_productListFragment_to_productDetailFragment)
        moneyHappyListAdapter = MoneyHappyAdapter(moneyHappyList,findNavController())
        productList.add(ProductxInfo(1000))
        productList.add(ProductxInfo(2000))
        productList.add(ProductxInfo(3000))
        productList.add(ProductxInfo(3200))
        productList.add(ProductxInfo(3100))
        productList.add(ProductxInfo(3040))
        binding.rvRecommanditemList.layoutManager = GridLayoutManager(requireContext(), 3)
        binding.rvRecommanditemList.adapter = productAdapter
        binding.rvMoneyHappyList.layoutManager = LinearLayoutManager(requireContext())
        binding.rvMoneyHappyList.adapter = moneyHappyListAdapter
    }

    fun backEvent(){
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (System.currentTimeMillis() - backPressedTime < 2000) {
                    requireActivity().finish() // 액티비티 종료
                } else {
                    backPressedTime = System.currentTimeMillis()
//                    showToast("한 번 더 누르면 종료됩니다.")
                    CommonUtils.showSingleLineCustomToast(requireContext(), ToastType.DEFAULT, "한 번 더 누르면 종료됩니다.")
                }
            }
        })
    }
}