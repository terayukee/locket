package com.ssafy.locket.ui.finance

import android.os.Bundle
import android.view.View
import com.ssafy.locket.BaseFragment
import com.ssafy.locket.R
import com.ssafy.locket.databinding.FragmentPaymentCalendarBinding

class PaymentCalendarFragment : BaseFragment<FragmentPaymentCalendarBinding>(
    FragmentPaymentCalendarBinding::bind,
    R.layout.fragment_payment_calendar
) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}