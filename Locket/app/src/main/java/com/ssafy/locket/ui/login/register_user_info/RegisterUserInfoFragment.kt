package com.ssafy.locket.ui.login.register_user_info

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.ssafy.locket.BaseFragment
import com.ssafy.locket.R
import com.ssafy.locket.databinding.FragmentRegisterUserInfoBinding


class RegisterUserInfoFragment : BaseFragment<FragmentRegisterUserInfoBinding>(
    FragmentRegisterUserInfoBinding::bind,
    R.layout.fragment_register_user_info
) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initEvent()
    }

    fun initEvent(){
        binding.btnNext.setOnClickListener {
            findNavController().navigate(R.id.action_registerUserInfoFragment_to_registerPasswordFragment)
        }
        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}