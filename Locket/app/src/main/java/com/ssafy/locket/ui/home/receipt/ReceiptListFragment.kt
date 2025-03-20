package com.ssafy.locket.ui.home.receipt

import android.os.Bundle
import android.view.View
import com.ssafy.locket.BaseFragment
import com.ssafy.locket.R
import com.ssafy.locket.databinding.FragmentReceiptListBinding

class ReceiptListFragment : BaseFragment<FragmentReceiptListBinding>(
    FragmentReceiptListBinding::bind,
    R.layout.fragment_receipt_list
) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}