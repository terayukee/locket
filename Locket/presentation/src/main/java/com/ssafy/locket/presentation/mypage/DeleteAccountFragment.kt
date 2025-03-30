package com.ssafy.locket.presentation.mypage

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.ssafy.locket.presentation.R
import com.ssafy.locket.presentation.base.BaseFragment
import com.ssafy.locket.presentation.databinding.FragmentDeleteAccountBinding
import com.ssafy.locket.presentation.login.LoginActivity

class DeleteAccountFragment : BaseFragment<FragmentDeleteAccountBinding>(
    FragmentDeleteAccountBinding::bind,
    R.layout.fragment_delete_account
) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initEvent()
    }

    fun initEvent(){
        binding.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.btnDelete.setOnClickListener {
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            requireActivity().finishAffinity() // 기존 스택 완전히 제거
        }
    }
}