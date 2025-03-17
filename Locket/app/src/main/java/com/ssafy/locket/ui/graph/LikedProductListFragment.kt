package com.ssafy.locket.ui.graph

import android.os.Bundle
import android.view.View
import com.ssafy.locket.BaseFragment
import com.ssafy.locket.R
import com.ssafy.locket.databinding.FragmentLikedProductListBinding

class LikedProductListFragment : BaseFragment<FragmentLikedProductListBinding>(
    FragmentLikedProductListBinding::bind,
    R.layout.fragment_liked_product_list
) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}