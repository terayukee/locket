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
import com.ssafy.locket.model.graph.Product
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
    private lateinit var productList: MutableList<Product>

    // 페이징 관련 변수
    private var isLoading = false
    private var currentPage = 1

    // 카테고리 번호
    private var productId = -1

    // ViewModel
    private val productViewModel: ProductViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initEvent()
        initAdapter()
        observeViewModel()
        initView()
    }

    private fun initEvent() {
        productId = arguments?.getInt("productId") ?: -1
        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun initAdapter() {
        productList = mutableListOf()
        productAdapter = ProductAdapter(
            productList,
            findNavController(),
            R.id.action_categoryProductListFragment_to_productDetailFragment
        )
        binding.rvProductList.layoutManager = GridLayoutManager(requireContext(), 3)
        binding.rvProductList.adapter = productAdapter

        // 스크롤 이벤트 리스너 추가 (페이징 처리)
        binding.rvProductList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as GridLayoutManager
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                // 스크롤이 끝에 도달하면 다음 페이지 로드
                if (!isLoading && firstVisibleItemPosition + visibleItemCount >= totalItemCount) {
                    loadMoreData()
                }
            }
        })
    }

    private fun initView() {
        productViewModel.getCategoryList(productId, currentPage)
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                productViewModel.productCategoryInfo.collect { productCategory ->
                    if (productCategory is ProductCategoryListState.Success) {
                        binding.tvTitle.text = productCategory.productCategoryList.categoryName
                        Log.d(TAG, productCategory.productCategoryList.products.toString())
                        if (currentPage == 1) {
                            productList.clear()
                        }
                        productList.addAll(productCategory.productCategoryList.products)
                        productAdapter.notifyDataSetChanged()
                        isLoading = false  // 데이터 로딩 완료
                    }
                }
            }
        }
    }

    private fun loadMoreData() {
        isLoading = true
        productViewModel.getCategoryList(productId, currentPage)
    }
}