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
import com.bumptech.glide.Glide
import com.ssafy.locket.model.graph.Product
import com.ssafy.locket.presentation.R
import com.ssafy.locket.presentation.base.BaseFragment
import com.ssafy.locket.presentation.databinding.FragmentSearchProductListBinding
import com.ssafy.locket.presentation.graph.adapter.ProductAdapter
import com.ssafy.locket.presentation.graph.viewmodel.ProductDetailState
import com.ssafy.locket.presentation.graph.viewmodel.ProductSearchState
import com.ssafy.locket.presentation.graph.viewmodel.ProductViewModel
import com.ssafy.locket.presentation.utils.CommonUtils
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

private const val TAG = "SearchProductListFragme"
class SearchProductListFragment : BaseFragment<FragmentSearchProductListBinding>(
    FragmentSearchProductListBinding::bind,
    R.layout.fragment_search_product_list
) {
    val productViewModel: ProductViewModel by activityViewModels()
    var page = 1

    private lateinit var productSearchAdapter: ProductAdapter
    private lateinit var productSearchList: MutableList<Product>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initEvent()
        initAdapter()
        getSearchInfo()
        lifecycleScope.launch {
            val name = productViewModel.productName.first()
            productViewModel.productSearch(name, page)
        }
    }

    fun initEvent(){
        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    fun initAdapter(){
        productSearchList = mutableListOf()
        productSearchAdapter = ProductAdapter(productSearchList,findNavController(),R.id.action_searchProductListFragment_to_productDetailFragment)
        binding.rvSearchList.layoutManager = GridLayoutManager(requireContext(), 3)
        binding.rvSearchList.adapter = productSearchAdapter
    }

    fun getSearchInfo(){
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                productViewModel.productSearchInfo.collect { productSearch ->
                    if(productSearch is ProductSearchState.Success) {
                        binding.tvProductTitle.text = productViewModel.productName.value
                        Log.d(TAG,productSearch.productSearchInfo.products.toString())
                        productSearchList.clear()
                        productSearchList.addAll(productSearch.productSearchInfo.products)
                        productSearchAdapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }
}