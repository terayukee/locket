package com.ssafy.locket.presentation.graph.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.ssafy.locket.presentation.R
import com.ssafy.locket.presentation.base.BaseFragment
import com.ssafy.locket.presentation.databinding.FragmentLikeProductListBinding
import com.ssafy.locket.presentation.graph.viewmodel.ProductCategoryListState
import com.ssafy.locket.presentation.graph.viewmodel.ProductLikeListState
import com.ssafy.locket.presentation.graph.viewmodel.ProductViewModel
import kotlinx.coroutines.launch

private const val TAG = "LikeProductListFragment"
class LikeProductListFragment : BaseFragment<FragmentLikeProductListBinding>(
    FragmentLikeProductListBinding::bind,
    R.layout.fragment_like_product_list
) {
    private val productViewModel: ProductViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initEvent()
        Log.d(TAG,"안녕하세요")
        productViewModel.getLikeList(1)
        getLikeList()
    }

    fun initEvent(){
        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    fun getLikeList(){
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                productViewModel.productLikeListInfo.collect { productLikeList ->
                    Log.d(TAG,productLikeList.toString())
                    if(productLikeList is ProductLikeListState.Success) {
                       Log.d(TAG,"좋아요")
                       Log.d(TAG,productLikeList.productLikeList.products.toString())
                    }
                }
            }
        }
    }
}