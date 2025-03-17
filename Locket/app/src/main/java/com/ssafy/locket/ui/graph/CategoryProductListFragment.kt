package com.ssafy.locket.ui.graph

import android.os.Bundle
import android.view.View
import com.ssafy.locket.BaseFragment
import com.ssafy.locket.R
import com.ssafy.locket.databinding.FragmentCategoryProductListBinding

class CategoryProductListFragment : BaseFragment<FragmentCategoryProductListBinding>(
    FragmentCategoryProductListBinding::bind,
    R.layout.fragment_category_product_list
) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}