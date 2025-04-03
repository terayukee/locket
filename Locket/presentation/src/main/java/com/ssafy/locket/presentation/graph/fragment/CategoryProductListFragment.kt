package com.ssafy.locket.presentation.graph.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.ssafy.locket.model.graph.ProductxInfo
import com.ssafy.locket.presentation.R
import com.ssafy.locket.presentation.base.BaseFragment
import com.ssafy.locket.presentation.databinding.FragmentCategoryProductListBinding
import com.ssafy.locket.presentation.graph.adapter.ProductAdapter
import com.ssafy.locket.presentation.graph.viewmodel.ProductCategoryListState
import com.ssafy.locket.presentation.graph.viewmodel.ProductViewModel
import kotlinx.coroutines.launch

private const val TAG = "CategoryProductListFrag"
class CategoryProductListFragment : BaseFragment<FragmentCategoryProductListBinding>(
    FragmentCategoryProductListBinding::bind,
    R.layout.fragment_category_product_list

) {
    private lateinit var productAdapter: ProductAdapter
    private lateinit var productList: MutableList<ProductxInfo>

    //카테고리 번호 알기 위함
    var productId  = -1
    //상품 뷰모델 사용
    private val productViewModel: ProductViewModel by activityViewModels()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initEvent()
        initAdapter()
        observeViewModel()
        initView()
    }

    fun initEvent(){
        productId = arguments?.getInt("productId") ?: -1
        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    fun initAdapter(){
        productList = mutableListOf()
        productAdapter = ProductAdapter(productList,findNavController(),R.id.action_categoryProductListFragment_to_productDetailFragment)
        productList.add(ProductxInfo(1000))
        productList.add(ProductxInfo(2000))
        productList.add(ProductxInfo(3000))
        productList.add(ProductxInfo(3200))
        productList.add(ProductxInfo(3100))
        productList.add(ProductxInfo(3040))
        binding.rvProductList.layoutManager = GridLayoutManager(requireContext(), 3)
        binding.rvProductList.adapter = productAdapter
    }

    fun initView(){
        Log.d(TAG,"클릭")
        Log.d(TAG,productId.toString())
        productViewModel.getCategoryList(productId,1)
    }

    fun observeViewModel(){
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                productViewModel.productCategoryInfo.collect { productCategory ->
                    if(productCategory is ProductCategoryListState.Success) {
                        binding.tvTitle.text = productCategory.productCategoryList.categoryName
                    }
                }
            }
        }
    }
}