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
import com.ssafy.locket.data.datasource.local.UserDataStoreSource
import com.ssafy.locket.model.graph.Product
import com.ssafy.locket.presentation.R
import com.ssafy.locket.presentation.base.BaseFragment
import com.ssafy.locket.presentation.databinding.FragmentLikeProductListBinding
import com.ssafy.locket.presentation.graph.adapter.ProductAdapter
import com.ssafy.locket.presentation.graph.viewmodel.ProductCategoryListState
import com.ssafy.locket.presentation.graph.viewmodel.ProductLikeListState
import com.ssafy.locket.presentation.graph.viewmodel.ProductViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "LikeProductListFragment"
@AndroidEntryPoint
class LikeProductListFragment : BaseFragment<FragmentLikeProductListBinding>(
    FragmentLikeProductListBinding::bind,
    R.layout.fragment_like_product_list
) {
    private val productViewModel: ProductViewModel by activityViewModels()
    private lateinit var productLikeAdapter: ProductAdapter
    private lateinit var productLikeList: MutableList<Product>

    @Inject
    lateinit var userDataStoreSource: UserDataStoreSource

    private var isLoading = false  // 중복 요청 방지
    private var userId: Int = 0    // 유저 ID 저장

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initEvent()
        initAdapter()
        getLikeList()
        lifecycleScope.launch {
            userId = (userDataStoreSource.userId.first() ?: 0).toInt()
            loadMoreData()  // 새로 로드
        }
    }


    private fun initAdapter() {
        productLikeList = mutableListOf()
        productLikeAdapter = ProductAdapter(
            productLikeList,
            findNavController(),
            R.id.action_likeProductListFragment_to_productDetailFragment
        )
        binding.rvLikeList.layoutManager = GridLayoutManager(requireContext(), 3)
        binding.rvLikeList.adapter = productLikeAdapter
    }

    private fun initEvent() {
        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun getLikeList() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                productViewModel.productLikeListInfo.collect { productLike ->
                    Log.d(TAG, productLike.toString())
                    if (productLike is ProductLikeListState.Success) {
                        productLikeList.clear()
                        Log.d(TAG, productLike.productLikeList.products.toString())
                        productLikeList.addAll(productLike.productLikeList.products)
                        productLikeAdapter.notifyDataSetChanged()
                        isLoading = false // 로딩 완료
                    }
                }
            }
        }
    }

    private fun loadMoreData() {
        isLoading = true
        lifecycleScope.launch {
            productViewModel.getLikeList(userId)
        }
    }
}