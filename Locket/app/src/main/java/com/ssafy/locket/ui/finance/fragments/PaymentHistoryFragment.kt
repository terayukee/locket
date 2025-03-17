package com.ssafy.locket.ui.finance.fragments

import android.os.Bundle
import android.view.View
import com.ssafy.locket.BaseFragment
import com.ssafy.locket.R
import com.ssafy.locket.databinding.FragmentPaymentHistoryBinding

class PaymentHistoryFragment : BaseFragment<FragmentPaymentHistoryBinding>(
    FragmentPaymentHistoryBinding::bind,
    R.layout.fragment_payment_history
) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}