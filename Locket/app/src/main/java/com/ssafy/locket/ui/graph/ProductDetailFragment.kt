package com.ssafy.locket.ui.graph

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.ssafy.locket.BaseFragment
import com.ssafy.locket.R
import com.ssafy.locket.databinding.FragmentProductDetailBinding

class ProductDetailFragment : BaseFragment<FragmentProductDetailBinding>(
    FragmentProductDetailBinding::bind,
    R.layout.fragment_product_detail
) {
    val bottomSheet = EditPriceBottomSheetFragment.newInstance()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initEvent()
    }

    fun initEvent(){
        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.cvNotificationSetting.setOnClickListener {
            bottomSheet.show(parentFragmentManager, EditPriceBottomSheetFragment.TAG)
        }
    }
}