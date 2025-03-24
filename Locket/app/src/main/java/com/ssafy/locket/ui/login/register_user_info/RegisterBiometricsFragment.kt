package com.ssafy.locket.ui.login.register_user_info

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.ssafy.locket.BaseFragment
import com.ssafy.locket.R
import com.ssafy.locket.databinding.FragmentRegisterBiometricsBinding

class RegisterBiometricsFragment : BaseFragment<FragmentRegisterBiometricsBinding>(
    FragmentRegisterBiometricsBinding::bind,
    R.layout.fragment_register_biometrics
) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initEvent()
    }

    fun initEvent(){
        binding.btnLater.setOnClickListener {
            findNavController().navigate(R.id.action_registerBiometricsFragment_to_homeFragment)
        }
        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}