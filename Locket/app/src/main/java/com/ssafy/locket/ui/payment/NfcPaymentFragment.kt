package com.ssafy.locket.ui.payment

import android.os.Bundle
import android.view.View
import com.ssafy.locket.BaseFragment
import com.ssafy.locket.R
import com.ssafy.locket.databinding.FragmentNfcPaymentBinding

class NfcPaymentFragment : BaseFragment<FragmentNfcPaymentBinding>(
    FragmentNfcPaymentBinding::bind,
    R.layout.fragment_nfc_payment
) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}