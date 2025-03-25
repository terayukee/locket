package com.ssafy.locket.ui.payment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ssafy.locket.BaseFragment
import com.ssafy.locket.R
import com.ssafy.locket.databinding.FragmentNfcPaymentBinding
import com.ssafy.locket.databinding.FragmentPaymentPasswordBinding

class PaymentPasswordFragment : BaseFragment<FragmentPaymentPasswordBinding>(
    FragmentPaymentPasswordBinding::bind,
    R.layout.fragment_payment_password
) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}