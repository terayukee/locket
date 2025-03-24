package com.ssafy.locket.ui.login.register_user_info

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.ssafy.locket.BaseFragment
import com.ssafy.locket.R
import com.ssafy.locket.databinding.FragmentRegisterPasswordBinding

class RegisterPasswordFragment : BaseFragment<FragmentRegisterPasswordBinding>(
    FragmentRegisterPasswordBinding::bind,
    R.layout.fragment_register_password
) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initEvent()
    }

    fun initEvent(){
        binding.btnNext.setOnClickListener {
            findNavController().navigate(R.id.action_registerPasswordFragment_to_confirmPasswordFragment)
        }
        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}