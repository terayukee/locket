package com.ssafy.locket.ui.login.register_user_info

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.ssafy.locket.BaseFragment
import com.ssafy.locket.R
import com.ssafy.locket.databinding.FragmentConfirmPasswordBinding

class ConfirmPasswordFragment : BaseFragment<FragmentConfirmPasswordBinding>(
    FragmentConfirmPasswordBinding::bind,
    R.layout.fragment_confirm_password
) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initEvent()
    }

    fun initEvent(){
        binding.btnNext.setOnClickListener {
            findNavController().navigate(R.id.action_confirmPasswordFragment_to_registerBiometricsFragment)
        }
        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}