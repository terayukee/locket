package com.ssafy.locket.ui.graph

import android.os.Bundle
import android.view.View
import com.ssafy.locket.BaseFragment
import com.ssafy.locket.R
import com.ssafy.locket.databinding.FragmentProductListBinding

class ProductListFragment : BaseFragment<FragmentProductListBinding>(
    FragmentProductListBinding::bind,
    R.layout.fragment_product_list
) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}