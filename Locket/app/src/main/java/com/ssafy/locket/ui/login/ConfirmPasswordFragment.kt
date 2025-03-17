package com.ssafy.locket.ui.login

import android.os.Bundle
import android.view.View
import com.ssafy.locket.BaseFragment
import com.ssafy.locket.R
import com.ssafy.locket.databinding.FragmentConfirmPasswordBinding

class ConfirmPasswordFragment : BaseFragment<FragmentConfirmPasswordBinding>(
    FragmentConfirmPasswordBinding::bind,
    R.layout.fragment_confirm_password
) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}