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
import androidx.recyclerview.widget.RecyclerView
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
    private val productViewModel: ProductViewModel by activityViewModels()
    private lateinit var productSearchAdapter: ProductAdapter
    private lateinit var productSearchList: MutableList<Product>

    private var isLoading = false  // 중복 요청 방지
    private var page = 1           // 현재 페이지 번호
    private var searchQuery: String = "" // 검색어 저장

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initEvent()
        initAdapter()
        getSearchInfo()
        initScrollListener()

        lifecycleScope.launch {
            searchQuery = productViewModel.productName.first()
            binding.tvProductTitle.text = searchQuery
            loadMoreData()  // 첫 페이지 데이터 로드
        }
    }

    private fun initEvent() {
        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun initAdapter() {
        productSearchList = mutableListOf()
        productSearchAdapter = ProductAdapter(
            productSearchList,
            findNavController(),
            R.id.action_searchProductListFragment_to_productDetailFragment
        )
        binding.rvSearchList.layoutManager = GridLayoutManager(requireContext(), 3)
        binding.rvSearchList.adapter = productSearchAdapter
    }

    private fun getSearchInfo() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                productViewModel.productSearchInfo.collect { productSearch ->
                    if (productSearch is ProductSearchState.Success) {
                        Log.d(TAG, "검색 결과: ${productSearch.productSearchInfo.products}")

                        if (page == 1) {
                            productSearchList.clear() // 첫 페이지면 초기화
                        }

                        productSearchList.addAll(productSearch.productSearchInfo.products)
                        productSearchAdapter.notifyDataSetChanged()
                        isLoading = false // 로딩 완료
                    }
                }
            }
        }
    }

    private fun initScrollListener() {
        binding.rvSearchList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as GridLayoutManager
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                // 스크롤이 리스트 끝에 도달하고, 로딩 중이 아닐 때 추가 데이터 요청
                if (!isLoading && (visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                    && firstVisibleItemPosition >= 0
                ) {
                    loadMoreData()
                }
            }
        })
    }

    private fun loadMoreData() {
        isLoading = true
        lifecycleScope.launch {
            productViewModel.productSearch(searchQuery, page)
            page++ // 다음 페이지 증가
        }
    }
}