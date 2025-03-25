package com.ssafy.locket.ui.mypage

import android.os.Bundle
import android.view.View
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.ssafy.locket.BaseFragment
import com.ssafy.locket.R
import com.ssafy.locket.databinding.FragmentDeleteAccountBinding

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
            val navOptions = NavOptions.Builder()
                .setPopUpTo(findNavController().graph.startDestinationId, true) // Clear all fragments, including the start destination
                .build()
            findNavController().navigate(R.id.action_deleteAccountFragment_to_signInFragment, null, navOptions)
        }
    }
}