package com.ssafy.locket.ui.finance

import android.os.Bundle
import android.view.View
import com.ssafy.locket.BaseFragment
import com.ssafy.locket.R
import com.ssafy.locket.databinding.FragmentFinanceBinding

class FinanceFragment : BaseFragment<FragmentFinanceBinding>(
    FragmentFinanceBinding::bind,
    R.layout.fragment_finance
) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}